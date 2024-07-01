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

package io.patriot_framework.generator.device.passive.sensors;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.patriot_framework.generator.dataFeed.DataFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for Sensor - device with single DataFeed.
 */
public abstract class AbstractSimpleSensor extends AbstractSensor implements SimpleSensor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimpleSensor.class);

    public AbstractSimpleSensor() {
    }

    public AbstractSimpleSensor(String label, DataFeed dataFeed) {
        super(label);
        dataFeed.setLabel(label);
        setDataFeed(dataFeed);
    }

    @Override
    @JsonSetter("dataFeed")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "className")
    public void setDataFeed(DataFeed dataFeed) {
        if (!getDataFeeds().isEmpty()) {
            removeDataFeed();
        }
        super.addDataFeed(dataFeed);
    }

    @Override
    public DataFeed getDataFeed() {
        return super.getDataFeeds().get(0);
    }

    @Override
    public void removeDataFeed() {
        DataFeed df = getDataFeed();
        super.removeDataFeed(df);
    }

    @Override
    public void addDataFeed(DataFeed dataFeed) {
        throw new UnsupportedOperationException("Only single data feed is allowed");
    }

}
