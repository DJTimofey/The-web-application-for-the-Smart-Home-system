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

package io.patriot_framework.generator.device;

import io.patriot_framework.generator.Data;
import io.patriot_framework.generator.controll.CoapController;
import io.patriot_framework.generator.events.DataObservable;
import io.patriot_framework.generator.network.NetworkAdapter;

import java.util.List;

/**
 * Base interface describing concept for device. Provides methods for manipulation and configuration.
 *
 * User should not use this interface for creating device objects. Mainly serves for internal object interaction,
 * but lacks DataFeed manipulation.
 */
public interface Device extends Unit {

    /**
     * Pulls data from data feed. This method serves as handler for data from DataFeed.
     * Forwards parameters for getNextValue method (see DataFeed.getNextValue).
     *
     * @param params for DataFeed computing
     * @return a list of generated values
     */
    List<Data> requestData(Object... params);

    /**
     * Enables CoaP communication with Device. Method should create tree of {@link org.eclipse.californium.core.server.resources.Resource}
     * endpoints and register them to CoaP Server.
     * Each distinct Device (e.g. Sensor, Actuator ... ) should define Resource tree.
     */
    void startCoapController();

    /**
     * Remove all {@link org.eclipse.californium.core.server.resources.Resource} endpoints for Device from CoaP Server,
     * in order to deny CoaP communication with instance of Device.
     */
    void stopCoapController();

    /**
     * Sets Network Adapter for device which enables sending data wi
     *
     * @param networkAdapter instance of NetworkAdapter
     */
    void setNetworkAdapter(NetworkAdapter networkAdapter);

    /**
     * Returns NetworkAdapter assigned to Device
     *
     * @return instance of Network Adapter
     */
    NetworkAdapter getNetworkAdapter();

    /**
     * Returns DataObservable for Device
     *
     * @return instance of DataObservable
     */
    DataObservable getDataObservable();

    /**
     * Sets DataObservable for Device.
     *
     * @param dataObservable instance of DataObservable
     */
    void setDataObservable(DataObservable dataObservable);

    /**
     * Returns CoapController for Device
     *
     * @return instacne of CoapController
     */
    CoapController getCoapController();

    /**
     * Sets CoapController for Device
     *
     * @param coapController instance of CoapController
     */
    void setCoapController(CoapController coapController);

}
