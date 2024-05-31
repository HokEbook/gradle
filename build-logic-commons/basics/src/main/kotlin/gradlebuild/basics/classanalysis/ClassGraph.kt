/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gradlebuild.basics.classanalysis


class ClassGraph(
    private val keepPackages: NameMatcher,
    private val unshadedPackages: NameMatcher,
    private val ignorePackages: NameMatcher,
    shadowPackage: String?
) {
    private val classesByPath: MutableMap<String, ClassDetails> = mutableMapOf()

    val classes: MutableMap<String, ClassDetails> = linkedMapOf()

    val entryPoints: MutableSet<ClassDetails> = linkedSetOf()

    private
    val shadowPackagePrefix = if (shadowPackage != null) shadowPackage.replace('.', '/') + "/" else ""

    /**
     * Returns the details for the given class, creating it if missing.
     *
     * @param className The _original_ name of the class, not the renamed name.
     */
    operator fun get(className: String): ClassDetails {
        return classes.getOrPut(className) {
            val jvmType = ignoredPackagePatterns.matches(className)
            val outputClassName = if (jvmType || unshadedPackages.matches(className)) className else shadowPackagePrefix + className
            val classDetails = ClassDetails(outputClassName)
            if (keepPackages.matches(className) && !ignorePackages.matches(className) && !jvmType) {
                entryPoints.add(classDetails)
            }
            classDetails
        }
    }

    fun visitClass(jarPath: String, className: String): ClassDetails {
        val details = get(className)
        classesByPath[jarPath] = details
        return details
    }

    fun forSourceEntry(jarPath: String): ClassDetails? {
        return classesByPath[jarPath]
    }
}


class ClassDetails(val outputClassName: String) {
    /**
     * The non-method dependencies of this type.
     */
    val dependencies = mutableSetOf<ClassDetails>()

    /**
     * The methods of this type.
     */
    val methods = mutableMapOf<String, MethodDetails>()

    val subtypes = mutableSetOf<ClassDetails>()

    val outputClassFilename
        get() = "$outputClassName.class"

    override fun toString(): String {
        return outputClassName
    }

    /**
     * Returns the method of this class with the given signature
     */
    fun method(name: String, descriptor: String): MethodDetails {
        val signature = name + descriptor
        return methods.getOrPut(signature) {
            MethodDetails(this, signature)
        }
    }

    /**
     * Returns the method of this class with the same signature as the given method.
     */
    fun method(methodDetails: MethodDetails): MethodDetails {
        val signature = methodDetails.signature
        return methods.getOrPut(signature) {
            MethodDetails(this, signature)
        }
    }

    fun superType(type: ClassDetails) {
        type.subtypes.add(this)
    }
}


class MethodDetails(val owner: ClassDetails, val signature: String) {
    val dependencies: MutableSet<MethodDetails> = linkedSetOf()

    override fun toString(): String {
        return "${owner.outputClassName}.$signature"
    }
}
