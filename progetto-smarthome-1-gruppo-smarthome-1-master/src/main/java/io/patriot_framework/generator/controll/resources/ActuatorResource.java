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

import io.patriot_framework.generator.Data;
import io.patriot_framework.generator.device.passive.actuators.Actuator;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;

public class ActuatorResource extends CoapResource {

    private Actuator actuator;

    public ActuatorResource(Actuator actuator) {
        super(actuator.getLabel());
        this.actuator = actuator;

        getAttributes().setTitle("Actuator resources");
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        exchange.accept();
        actuator.controlSignal();

        exchange.respond(CoAP.ResponseCode.CHANGED);
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        exchange.accept();
        List<Data> state = actuator.requestData();
        String result = state.get(0).get(String.class);

        exchange.respond(CoAP.ResponseCode.CONTENT,result);
    }

}
