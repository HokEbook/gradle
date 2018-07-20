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

package org.gradle.kotlin.dsl.cache

import org.gradle.caching.BuildCacheKey
import org.gradle.caching.internal.controller.BuildCacheLoadCommand
import org.gradle.caching.internal.controller.BuildCacheStoreCommand

import java.io.File
import java.io.InputStream
import java.io.OutputStream


class ScriptBuildCacheKey(
    private val displayName: String,
    private val cacheKey: String
) : BuildCacheKey {

    override fun getDisplayName(): String = displayName

    override fun getHashCode(): String = cacheKey
}


/**
 * Loads a directory previously stored by [StoreDirectory] from the build cache.
 */
class LoadDirectory(
    private val directory: File,
    private val cacheKey: BuildCacheKey
) : BuildCacheLoadCommand<Unit> {

    override fun getKey(): BuildCacheKey = cacheKey

    override fun load(inputStream: InputStream): BuildCacheLoadCommand.Result<Unit> {

        val entryCount = unpack(inputStream, directory)

        return object : BuildCacheLoadCommand.Result<Unit> {
            override fun getMetadata() = Unit
            override fun getArtifactEntryCount(): Long = entryCount
        }
    }
}


/**
 * Stores a directory in the build cache.
 */
class StoreDirectory(
    private val directory: File,
    private val cacheKey: BuildCacheKey
) : BuildCacheStoreCommand {

    override fun getKey(): BuildCacheKey = cacheKey

    override fun store(outputStream: OutputStream): BuildCacheStoreCommand.Result {

        val entryCount = pack(directory, outputStream)

        return BuildCacheStoreCommand.Result { entryCount }
    }
}
