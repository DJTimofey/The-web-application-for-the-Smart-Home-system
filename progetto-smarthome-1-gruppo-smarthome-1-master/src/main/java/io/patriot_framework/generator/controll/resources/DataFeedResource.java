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

package io.patriot_framework.generator.controll.resources;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.smarthome.device.Actuator;
import io.patriot_framework.generator.dataFeed.DataFeed;
import io.patriot_framework.generator.dataFeed.DataFeedBean;
import io.patriot_framework.generator.device.passive.sensors.Sensor;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DataFeedResource extends CoapResource {

    private Sensor sensor;

    private ObjectMapper mapper;

    public DataFeedResource(Sensor sensor) {
        super("dataFeed");
        this.sensor = sensor;

        JsonFactory factory = new JsonFactory();
        mapper = new ObjectMapper(factory);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        List<DataFeed> df = sensor.getDataFeeds();
        exchange.respond(df.toString());
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();
        String body = exchange.getRequestText();

        DataFeedBean dataFeed = null;
        try {
            dataFeed = mapper.readValue(body, DataFeedBean.class);
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Actuator.class.getName());

            logger.log(Level.INFO, "Exception: " + e.getMessage());
        }
        
        if(dataFeed != null) {
        	try {
        		sensor.addDataFeed(dataFeed.getDataFeed());
        	} catch (NullPointerException e) {
                Logger logger = Logger.getLogger(Actuator.class.getName());

                logger.log(Level.INFO, "Exception: " + e.getMessage());
        	}
        }
        exchange.respond(CoAP.ResponseCode.CHANGED);
    }

}
