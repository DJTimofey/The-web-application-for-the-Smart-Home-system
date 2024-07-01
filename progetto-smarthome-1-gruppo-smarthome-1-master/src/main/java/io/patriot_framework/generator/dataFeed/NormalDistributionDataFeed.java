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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.patriot_framework.generator.Data;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.randvar.NormalGen;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.RandomStream;

/**
 * DataFeed based on Normal Distribution from SSJ library.
 */
public class NormalDistributionDataFeed implements DataFeed {

    private String label;

    private NormalGen normalGen;

    private NormalDist normalDist;

    private double previousValue;

    @JsonCreator
    public NormalDistributionDataFeed(@JsonProperty("mu") double mu, @JsonProperty("sigma") double sigma) {
        RandomStream rs = new MRG32k3a();
        this.normalGen = new NormalGen(rs, mu, sigma);
        this.normalDist = new NormalDist(mu, sigma);
    }

    /**
     * Generates next value for Normal Distribution.
     * If method is called with 0 params, then it generates random variate from normal distribution.
     * If method is called with 1 or more arguments, distribution function evaluated at x = param[0]
     *
     * @param params first param represents x value at which the distribution function is evaluated
     * @return next evaluated value
     */
    @Override
    public Data getNextValue(Object... params) {
        Double result = null;

//        if(params.length == 0) {
            result = normalGen.nextDouble();
//        } else {
//            result = normalDist.cdf((Double) params[0]);
//        }
        previousValue = result;

        Data d= new Data(Double.class, result);
        d.setLabel(label);

        return d;
    }

    @Override
    public Data getPreviousValue() {
        return new Data(Double.class, previousValue);
    }

    @Override
    public void setLabel(String label) {
        this.label=label;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
