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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.patriot_framework.generator.Data;
import io.patriot_framework.generator.dataFeed.ConstantDataFeed;
import io.patriot_framework.generator.dataFeed.DataFeed;
import io.patriot_framework.generator.device.Device;
import io.patriot_framework.generator.device.impl.basicDevices.Thermometer;
import io.patriot_framework.generator.device.passive.sensors.SimpleSensor;
import io.patriot_framework.generator.network.NetworkAdapter;
import io.patriot_framework.generator.network.Rest;
import io.patriot_framework.generator.network.wrappers.JSONWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActiveDeviceImpl implements ActiveDevice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDeviceImpl.class);

    private Timer timer = new Timer();

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "className")
    private DataFeed timeFeed;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "className")
    private Device device;

    public ActiveDeviceImpl() {
    }

    public ActiveDeviceImpl(DataFeed timeFeed, Device device) {
        this.timeFeed = timeFeed;
        this.device = device;
    }

    public ActiveDeviceImpl(String label, String categoria, DataFeed df, double timer) {

        String endopint = "http://localhost:8080/" + categoria;

        NetworkAdapter na = new Rest(endopint, new JSONWrapper());

        SimpleSensor device = new Thermometer(label, df);
        device.setNetworkAdapter(na);
        device.startCoapController();

        this.timeFeed = new ConstantDataFeed(timer);
        this.device = device;
    }

    @Override
    public void start() {
        timer.schedule(task(), 0);
        LOGGER.info("Device: " + device.getLabel() + " started active simulation");
    }

    @Override
    public void stop() {
        timer.cancel();
        timer.purge();
        LOGGER.info("Device: " + device.getLabel() + " stopped active simulation");
    }

    private TimerTask task() {
        return new TimerTask() {
            @Override
            public void run() {
                double simTime = timeFeed.getNextValue().get(Double.class);
                LOGGER.info("Next clock for device: " + device.getLabel() + " is in seconds: " + simTime);

                List<Data> d = device.requestData(simTime);           
                System.out.println(d.get(0).toJson());
                //

                timer.schedule(task(), Math.round(simTime));
            }
        };
    }

    @Override
    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public void setTimeFeed(DataFeed timeFeed) {
        this.timeFeed = timeFeed;
    }

    @Override
    public DataFeed getTimeFeed() {
        return timeFeed;
    }

}
