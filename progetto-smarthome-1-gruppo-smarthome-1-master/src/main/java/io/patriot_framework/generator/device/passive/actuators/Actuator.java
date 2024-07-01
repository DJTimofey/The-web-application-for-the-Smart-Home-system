/*
 * Copyright 2019 Patriot project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.patriot_framework.generator.device.passive.actuators;

import io.patriot_framework.generator.device.Device;

/**
 * Actuator - "part of a machine or system that moves something or makes something work".
 * This interface provides methods for
 */
public interface Actuator extends Device {

    /**
     * Actuator is waiting for impulse to start simulation. After control signal it should change state for set duration of time.
     *
     */
    void controlSignal();

    /**
     * Returns instance of StateMachine
     *
     * @return instance of StateMachine
     */
    StateMachine getStateMachine();

    /**
     * Sets StateMachine for Actuator
     *
     * @param stateMachine machine internally used by actuator
     */
    void setStateMachine(StateMachine stateMachine);

}
