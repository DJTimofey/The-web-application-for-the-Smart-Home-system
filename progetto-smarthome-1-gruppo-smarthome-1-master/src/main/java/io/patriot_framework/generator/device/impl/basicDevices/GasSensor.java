package io.patriot_framework.generator.device.impl.basicDevices;

import io.patriot_framework.generator.dataFeed.DataFeed;
import io.patriot_framework.generator.device.passive.sensors.AbstractSimpleSensor;

public class GasSensor extends AbstractSimpleSensor {
	public GasSensor(String label, DataFeed datafeed) {
		super(label, datafeed);
	}
	
}
