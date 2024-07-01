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

/**
 * State nodes for StateMachine. Used for creating loop of states to simulate actions of Actuator.
 */
public class State {

    /**
     * Name of representing state (e.g. Started, Stopped, Extracting...)
     */
    private String value;

    /**
     * If state should last set about of time duration should be greater than 0.0
     */
    private double duration;

    /**
     * For better readability State holds information, if it last exact time or not.
     */
    private boolean isTimeDependent;

    public State(String value, double duration) {
        this.value = value;
        this.duration = duration;
        this.isTimeDependent = true;
    }

    public State(String value) {
        this.value = value;
        this.duration = 0.0;
        this.isTimeDependent = false;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public boolean isTimeDependent() {
        return isTimeDependent;
    }

    public void setTimeDependent(boolean timeDependent) {
        this.isTimeDependent = timeDependent;
    }

    @Override
    public String toString() {
        return "State{" +
                "value='" + value + '\'' +
                ", duration=" + duration +
                '}';
    }
}