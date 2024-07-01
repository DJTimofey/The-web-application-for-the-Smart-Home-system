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

package io.patriot_framework.generator.device.active;

import io.patriot_framework.generator.dataFeed.DataFeed;
import io.patriot_framework.generator.device.Device;

/**
 * This interface provides possibility to simulate Device in time.
 * Time intervals of data polling are determined from DataFeed.
 *
 * As Time Simulations requests data from Device with series of continuous tasks,
 * the generated value itself is lost, therefore user is not able to get it as return value.
 * This behavior requires use of DataObservable class and Observer Pattern.
 */
public interface ActiveDevice {

    /**
     * Starts simulation process.
     */
    void start();

    /**
     * Stops simulation.
     */
    void stop();

    /**
     * Sets Device for TimeSimulation
     *
     * @param device instance of Device
     */
    void setDevice(Device device);

    /**
     * Gets Device for TimeSimulation
     *
     * @return instance of Device
     */
    Device getDevice();

    /**
     * Sets DataFeed for TimeSimulation
     *
     * @param timeFeed instance of DataFeed
     */
    void setTimeFeed(DataFeed timeFeed);

    /**
     * Returns DataFeed for TimeSimulation
     *
     * @return instance of DataFeed
     */
    DataFeed getTimeFeed();

}
