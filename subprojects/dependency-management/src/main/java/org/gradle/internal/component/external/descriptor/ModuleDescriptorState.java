/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.internal.component.external.descriptor;

import org.gradle.api.artifacts.component.ModuleComponentIdentifier;

/**
 * Pieces of an ivy module descriptor, which are here as we migrate away from the Ivy model
 */
public class ModuleDescriptorState {
    // The identifier extracted from the descriptor itself. May be different to the id of the containing module
    private final ModuleComponentIdentifier componentIdentifier;
    private final String status;
    private final boolean generated;

    public ModuleDescriptorState(ModuleComponentIdentifier componentIdentifier, String status, boolean generated) {
        this.componentIdentifier = componentIdentifier;
        this.status = status;
        this.generated = generated;
    }

    public ModuleComponentIdentifier getComponentIdentifier() {
        return componentIdentifier;
    }

    public boolean isGenerated() {
        return generated;
    }

    public String getStatus() {
        return status;
    }
}
