/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.api.problems.internal;

import org.gradle.api.problems.ProblemReporter;
import org.gradle.internal.operations.CurrentBuildOperationRef;
import org.gradle.internal.service.scopes.Scope;
import org.gradle.internal.service.scopes.ServiceScope;
import org.gradle.problems.buildtree.ProblemStream;

import java.util.Collection;

@ServiceScope(Scope.BuildTree.class)
public class DefaultProblems implements InternalProblems {

    private final ProblemStream problemStream;
    private final CurrentBuildOperationRef currentBuildOperationRef;
    private final ExceptionProblemContainer exceptionProblemContainer;
    private final Collection<ProblemEmitter> emitter;
    private final InternalProblemReporter internalReporter;
    private final AdditionalDataBuilderFactory additionalDataBuilderFactory = new AdditionalDataBuilderFactory();

    public DefaultProblems(Collection<ProblemEmitter> emitter, CurrentBuildOperationRef currentBuildOperationRef) {
        this(emitter, null, currentBuildOperationRef, new DefaultExceptionProblemContainer());
    }

    public DefaultProblems(Collection<ProblemEmitter> emitter) {
        this(emitter, null, CurrentBuildOperationRef.instance(), new DefaultExceptionProblemContainer());
    }

    public DefaultProblems(Collection<ProblemEmitter> emitter, ProblemStream problemStream, CurrentBuildOperationRef currentBuildOperationRef, ExceptionProblemContainer exceptionProblemContainer) {
        this.emitter = emitter;
        this.problemStream = problemStream;
        this.currentBuildOperationRef = currentBuildOperationRef;
        this.exceptionProblemContainer = new DefaultExceptionProblemContainer();
        internalReporter = createReporter(emitter, problemStream, exceptionProblemContainer);
    }

    @Override
    public ProblemReporter getReporter() {
        return createReporter(emitter, problemStream, exceptionProblemContainer);
    }

    private DefaultProblemReporter createReporter(Collection<ProblemEmitter> emitter, ProblemStream problemStream, ExceptionProblemContainer exceptionProblemContainer) {
        return new DefaultProblemReporter(emitter, problemStream, currentBuildOperationRef, exceptionProblemContainer, additionalDataBuilderFactory);
    }

    @Override
    public InternalProblemReporter getInternalReporter() {
        return internalReporter;
    }

    @Override
    public AdditionalDataBuilderFactory getAdditionalDataBuilderFactory() {
        return additionalDataBuilderFactory;
    }
}
