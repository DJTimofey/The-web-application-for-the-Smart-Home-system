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

package io.patriot_framework.generator.controll;

import io.patriot_framework.generator.controll.resources.ActuatorResource;
import io.patriot_framework.generator.device.passive.actuators.Actuator;
import org.eclipse.californium.core.CoapResource;

public class ActuatorCoapController implements CoapController {

    private CoapDeviceControlServer server;

    private Actuator actuator;

    private ActuatorResource resource = null;

    public ActuatorCoapController(Actuator actuator) {
        this.actuator = actuator;
        server = CoapDeviceControlServer.getInstance();
    }

    @Override
    public void registerDevice() {
        server.add(createResource());
    }

    @Override
    public void removeDevice() {
        server.remove(resource);
        resource = null;
    }

    private CoapResource createResource() {
        if(resource == null) {
            resource = new ActuatorResource(actuator);
        }

        return resource;
    }


}
