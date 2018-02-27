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

package org.gradle.tooling.internal.provider;

import org.gradle.tooling.internal.provider.serialization.SerializedPayload;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Result of one of the actions of a phased action. Must be serializable since will be dispatched to client.
 */
public class PhasedBuildActionResult implements Serializable {
    @Nullable public final SerializedPayload result;
    @Nullable public final SerializedPayload failure;

    public final Type type;

    public PhasedBuildActionResult(@Nullable SerializedPayload result, @Nullable SerializedPayload failure, Type type) {
        this.result = result;
        this.failure = failure;
        this.type = type;
    }

    /**
     * Phases of the build when it is possible to run an action provided by the client.
     */
    public enum Type {
        AFTER_LOADING,
        AFTER_CONFIGURATION,
        AFTER_BUILD
    }
}
