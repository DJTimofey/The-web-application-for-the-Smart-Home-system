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

package io.patriot_framework.generator.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarthome.smarthome.device.Actuator;
import io.patriot_framework.generator.Data;
import io.patriot_framework.generator.network.wrappers.DataWrapper;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MQTT implements NetworkAdapter {

    private String publisherId = UUID.randomUUID().toString();

    private IMqttAsyncClient client;

    public MQTT(@JsonProperty("serverURI") String serverURI) {
        connect(serverURI);
    }

    private void connect(String serverURI) {
        try {
            client = new MqttAsyncClient(serverURI, publisherId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);
        } catch (MqttException e) {
            Logger logger = Logger.getLogger(Actuator.class.getName());

            logger.log(Level.INFO, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void send(List<Data> data) {

    }

    @Override
    public void setDataWrapper(DataWrapper dataWrapper) {

    }

    @Override
    public DataWrapper getDataWrapper() {
        return null;
    }
}
