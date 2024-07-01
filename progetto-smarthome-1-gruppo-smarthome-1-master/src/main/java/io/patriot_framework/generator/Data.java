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

package io.patriot_framework.generator;

import org.json.JSONObject;

import io.patriot_framework.generator.device.Unit;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Class provides simple tool to store data in different data types.
 * To give users freedom in designing Devices and DataFeeds library offers
 * Data structure to save and safety manipulate with generated data.
 */
public class Data {

    /**
     * Class representing data
     */
    private Class<?> dataClazz;

    /**
     * Actual object containing data
     */
    private Object data;

    private String label;

    /**
     * Constructor
     * @param dataClazz class of stored object
     * @param data stored data
     */
    public Data(Class<?> dataClazz, Object data) {
        this.dataClazz = dataClazz;
        this.data = data;
    }

    /**
     * Constructor
     * @param dataClazz class of data stored in object
     */
    public Data(Class<?> dataClazz) {
        this.dataClazz = dataClazz;
    }

    /**
     * Gets data saved within Data instance with desired type. If their type is not castable to provided Class,
     * method throws IllegalCastException.
     *
     * @param clazz class used to retype the object to
     * @param <T> type of class to be used
     * @return retyped stored value to new class
     */
    public <T> T get(Class<T> clazz) {
        if(dataClazz.equals(clazz)) {
            return clazz.cast(data);
        }
        return null;
    }

    /**
     * Setter
     *
     * @param obj object to be set
     * @param clazz class of object to be set
     * @param <T> type of class
     * @return retyped object, which was set to target class
     */
    public <T> T set(T obj, Class<T> clazz) {
        if(dataClazz.equals(clazz)) {
            data = obj;
            return clazz.cast(data);
        }

        return null;
    }

    @Override
    public String toString() {

        String jsonString = new JSONObject()
                  .put("data", new JSONObject()
                    .put("dataClass", dataClazz)
                    .put("device", label)
                    .put("value", data.toString()))
                  .toString();


        return jsonString;
    }

    public String toJson() {

        String jsonString = new JSONObject()
                  .put("data", new JSONObject()
                    .put("dataClass", dataClazz)
                    .put("device", label)
                    .put("value", data.toString()))
                  .toString();


        return jsonString;
    }

    public void setLabel(String label){
        this.label=label;
    }
}
