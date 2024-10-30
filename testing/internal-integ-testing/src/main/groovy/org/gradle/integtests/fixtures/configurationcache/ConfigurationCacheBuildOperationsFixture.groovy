/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.integtests.fixtures.configurationcache


import org.gradle.integtests.fixtures.BuildOperationTreeQueries
import org.gradle.internal.configurationcache.ConfigurationCacheLoadBuildOperationType
import org.gradle.internal.configurationcache.ConfigurationCacheStoreBuildOperationType
import org.gradle.internal.operations.trace.BuildOperationRecord

import javax.annotation.Nullable

import static org.hamcrest.CoreMatchers.notNullValue
import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.MatcherAssert.assertThat

class ConfigurationCacheBuildOperationsFixture {

    final BuildOperationTreeQueries operations

    ConfigurationCacheBuildOperationsFixture(BuildOperationTreeQueries operations) {
        this.operations = operations
    }

    boolean getReused() {
        workGraphStoreOperation() == null && workGraphLoadOperation() != null
    }

    void assertStateLoaded() {
        def load = workGraphLoadOperation()
        assertThat(load, notNullValue())
        assertThat(load.failure, nullValue())
        assertThat(workGraphStoreOperation(), nullValue())
    }

    void assertStateLoadFailed() {
        def load = workGraphLoadOperation()
        assertThat(load, notNullValue())
        assertThat(load.failure, notNullValue())
        assertThat(workGraphStoreOperation(), nullValue())
    }

    void assertStateStored(boolean expectLoad = true) {
        def store = workGraphStoreOperation()
        assertThat(store, notNullValue())
        assertThat(store.failure, nullValue())
        assertThat(workGraphLoadOperation(), expectLoad ? notNullValue() : nullValue())
    }

    void assertStateStoreFailed() {
        assertThat(workGraphLoadOperation(), nullValue())
        def store = workGraphStoreOperation()
        assertThat(store, notNullValue())
        assertThat(store.failure, notNullValue())
    }

    void assertModelStored() {
        def modelStore = modelStoreOperation()
        assert modelStore != null && modelStore.failure == null
        assert modelLoadOperation() == null
    }

    void assertModelStoreFailed() {
        def modelStore = modelStoreOperation()
        assert modelStore != null && modelStore.failure != null
        assert modelLoadOperation() == null
    }

    void assertModelLoaded() {
        def modelLoad = modelLoadOperation()
        assert modelLoad != null && modelLoad.failure == null
        assert modelStoreOperation() == null
    }

    void assertNoConfigurationCache() {
        assertNoWorkGraphStore()
        assertNoModelStore()
    }

    void assertNoWorkGraphStore() {
        assert workGraphStoreOperation() == null
        assert workGraphLoadOperation() == null
    }

    void assertNoModelStore() {
        assert modelStoreOperation() == null
        assert modelLoadOperation() == null
    }

    @Nullable
    private BuildOperationRecord workGraphStoreOperation() {
        operations.singleOrNone(ConfigurationCacheStoreBuildOperationType)
    }

    @Nullable
    private BuildOperationRecord workGraphLoadOperation() {
        operations.singleOrNone(ConfigurationCacheLoadBuildOperationType)
    }

    @Nullable
    private BuildOperationRecord modelStoreOperation() {
        operations.singleOrNone("Store model in configuration cache")
    }

    @Nullable
    private BuildOperationRecord modelLoadOperation() {
        operations.singleOrNone("Load model from configuration cache")
    }
}
