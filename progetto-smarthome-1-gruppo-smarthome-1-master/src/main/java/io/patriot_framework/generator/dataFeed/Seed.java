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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

/**
 * SSJ uses RandomStreams to generate uniform variates (real numbers) over the interval (0,1).
 * Seed is array of 6 integers, that provides initial state for RandomStreams. It is starting point for computing.
 * As initial seed for RandomStreams SSJ library uses vector [0..5]. This ensures,
 * that repetition of number generation returns same values. For test diversity we need tool that will provide different,
 * but repeatable results.
 */
public class Seed {

    private static final long M1 = 4294967087L;
    private static final long M2 = 4294944443L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Seed.class);

    public static long[] generate() {
        long[] seed = {random(M1), random(M1), random(M1), random(M2), random(M2), random(M2)};

        LOGGER.info("New seed is generated: " + seed);

        return seed;
    }

    private static long random(long limit)
    {
        SecureRandom random = new SecureRandom();

        return 1 + (long) (random.nextFloat() * (limit - 1));
    }

}
