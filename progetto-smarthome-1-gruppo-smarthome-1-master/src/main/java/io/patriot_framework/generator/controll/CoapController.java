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

/**
 * The Constrained Application Protocol (CoAP) is a specialized web
 * transfer protocol for use with constrained nodes and constrained
 * (e.g., low-power, lossy) networks.
 * See: https://tools.ietf.org/html/rfc7252
 *
 * This interface offers methods to control and communicate with instance of Device.
 * CoapController and Device should have one-to-one relationship, as class that implements
 * this interface is responsible for creating and adding CoapResource to instance of CoapServer.
 *
 * Different Devices may offer access to different resources, therefore CoapController classes
 * should offer diversity to support this behavior.
 */
public interface CoapController {

    /**
     * Creates resource endpoints for Device and adds them to CoapServer, which enables communication
     * with Device.
     */
    void registerDevice();

    /**
     * Removes resource endpoints from CoapServer to deny any connection with Device via CoAP.
     */
    void removeDevice();

}
