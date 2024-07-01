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

package io.patriot_framework.generator.dataFeed;

import io.patriot_framework.generator.Data;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * DataFeed provides estimation of hourly temperature based on Maximum and Minimum temperature, that was measured in one day.
 * For calculation method uses T_MIN and T_max, which are hours, where ma maximum and minimum temperature occurred within one day.
 * With addition of dayMin and dayMax, minimal and maximal temperatures of the day.
 *
 * DataFeed is based on algorithm from  https://www.researchgate.net/publication/245383326_New_algorithm_for_generating_hourly_temperature_values_using_daily_maximum_minimum_and_average_values_from_climate_models
 */
public class DayTemperatureDataFeed implements DataFeed {

    private String label;

    /**
     * Minimal temperature of the day
     */
    private double dayMin;
    /**
     * Maximal temperature of the day
     */
    private double dayMax;

    /**
     * Hour, in which minimal temperature occurs.
     */
    private static final int T_MIN = 5;
    /**
     * Hour, in which maximal temperature occurs.
     */
    private static final int T_MAX = 16;

    private static final Expression F1 = new ExpressionBuilder("( cos( pi * ("+ T_MIN +" - t ) / 24 + pi * " +  T_MIN + "/24 - pi * " + T_MAX + "/24) + 1 ) / 2")
        .variable("t")
        .build();

    private static final Expression F2 = new ExpressionBuilder("(cos(pi * (t-" + T_MIN + ")/" + T_MAX + "-" + T_MIN + ")+1)/2")
        .variable("t")
        .build();

    private static final Expression F3 = new ExpressionBuilder("(cos(pi(24+" + T_MIN + "-t)/24+" + T_MIN + "-"+ T_MAX + ")+1)/2")
        .variable("t")
        .build();

    private double lastValue;

    public DayTemperatureDataFeed(float dayMin, float dayMax) {
        this.dayMin = dayMin;
        this.dayMax = dayMax;
    }

    @Override
    public Data getNextValue(Object... params) {
        double time = (double) params[0];
        double result = 0;

        time = (time/1000) % 24;

        if(time < T_MIN) {
            F1.setVariable("t", time);
            double a = F1.evaluate();
            double second = 1 - a;
            result = (a * dayMin) + (second * dayMax);
        } else if(T_MIN < time && time < T_MAX) {
            F2.setVariable("t", time);
            double a = F2.evaluate();
            double second = 1 -a;
            result = (a * dayMin) + (second * dayMax);
        } else if(T_MIN < time) {
            F3.setVariable("t", time);
            double a = F3.evaluate();
            double second = 1 - a;
            result = (a * dayMin) + (second * dayMax);
        }

        lastValue = result;

        Data d = new Data(Double.class, result);
        d.setLabel(label);

        return d;
    }

    @Override
    public Data getPreviousValue() {
        return new Data(Double.class, lastValue);
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
