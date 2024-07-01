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

package io.patriot_framework.generator.network.wrappers;

import io.patriot_framework.generator.Data;

import java.util.List;

/**
 * Interface provides method for wrapping all important data in to properly documented format,
 * design mainly for data transfer. Format should contain generated data and all necessary information,
 * to identify their origin.
 */
public interface DataWrapper {

    /**
     * Wraps data into specific format
     *
     * @param data generated from device
     * @return String representation of data format
     */
    String wrapData(List<Data> data);

}
