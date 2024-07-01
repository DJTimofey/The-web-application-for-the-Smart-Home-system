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

package io.patriot_framework.generator.dataFeed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.patriot_framework.generator.Data;

/**
 * Data stream values from this feed follows linear function.
 */
public class LinearDataFeed implements DataFeed {

    private String label;

    /**
     * Volume of growth
     */
    private double period;

    /**
     * Point (value), from which DataFeed starts
     */
    private double currentTime;

    @JsonCreator
    public LinearDataFeed(@JsonProperty("period") double period) {
        this.period = period;
        currentTime = 0;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public Data getNextValue(Object... params) {
        currentTime += period;

        return new Data(Double.class, currentTime);
    }

    @Override
    public Data getPreviousValue() {
        return new Data(Double.class, currentTime - period);
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
