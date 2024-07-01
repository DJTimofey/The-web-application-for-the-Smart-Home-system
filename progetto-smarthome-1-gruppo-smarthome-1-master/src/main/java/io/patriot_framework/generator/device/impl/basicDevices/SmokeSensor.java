package io.patriot_framework.generator.device.impl.basicDevices;

import io.patriot_framework.generator.dataFeed.DataFeed;
import io.patriot_framework.generator.device.passive.sensors.AbstractSimpleSensor;

public class SmokeSensor extends AbstractSimpleSensor{
	public SmokeSensor (String label, DataFeed datafeed) {
		super(label, datafeed);
	}
}
