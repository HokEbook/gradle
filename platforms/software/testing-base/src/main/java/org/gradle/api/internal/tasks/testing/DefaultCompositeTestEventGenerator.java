/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.api.internal.tasks.testing;

import org.gradle.api.NonNullApi;
import org.gradle.api.tasks.testing.CompositeTestEventGenerator;
import org.gradle.api.tasks.testing.TestEventGenerator;
import org.gradle.api.tasks.testing.TestFailure;
import org.gradle.api.tasks.testing.TestOutputEvent;
import org.gradle.api.tasks.testing.TestResult;
import org.gradle.internal.id.IdGenerator;

import javax.annotation.Nullable;
import java.time.Instant;

@NonNullApi
public final class DefaultCompositeTestEventGenerator implements CompositeTestEventGenerator {
    private final TestResultProcessor processor;
    private final IdGenerator<?> idGenerator;
    private final @Nullable TestDescriptorInternal parent;
    private final TestDescriptorInternal testDescriptor;

    public DefaultCompositeTestEventGenerator(
        TestResultProcessor processor, IdGenerator<?> idGenerator, @Nullable TestDescriptorInternal parent, TestDescriptorInternal testDescriptor
    ) {
        this.processor = processor;
        this.idGenerator = idGenerator;
        this.parent = parent;
        this.testDescriptor = testDescriptor;
    }

    @Override
    public TestEventGenerator createSolitaryNested(String name, String displayName) {
        return new DefaultCompositeTestEventGenerator(
            processor, idGenerator, testDescriptor, new DefaultTestDescriptor(idGenerator.generateId(), null, name, null, displayName)
        );
    }

    @Override
    public CompositeTestEventGenerator createCompositeNested(String name) {
        return new DefaultCompositeTestEventGenerator(
            processor, idGenerator, testDescriptor, new DefaultTestSuiteDescriptor(idGenerator.generateId(), name)
        );
    }

    @Override
    public void started(Instant startTime) {
        processor.started(testDescriptor, new TestStartEvent(startTime.toEpochMilli(), parent == null ? null : parent.getId()));
    }

    @Override
    public void output(TestOutputEvent.Destination destination, String output) {
        processor.output(testDescriptor.getId(), new DefaultTestOutputEvent(destination, output));
    }

    @Override
    public void failure(TestFailure failure) {
        processor.failure(testDescriptor.getId(), failure);
    }

    @Override
    public void completed(Instant endTime, TestResult.ResultType resultType) {
        processor.completed(testDescriptor.getId(), new TestCompleteEvent(endTime.toEpochMilli(), resultType));
    }

    @Override
    public void close() {
    }
}
