package io.patriot_framework.generator.dataFeed;

import java.util.Random;

import io.patriot_framework.generator.Data;

public class BinaryDataFeed implements DataFeed {
	private int prob;
	private String label;
	private double lastValue;
	private Random random;
	
	public BinaryDataFeed(int n) {
		this.prob = n;
		this.random = new Random();
	}
	
	@Override
	public Data getNextValue(Object... params) {
		double result;
		if (random.nextInt(prob) == 0) {
			result = 1.0;
		} else {
			result = 0.0;
		}
		lastValue = result;

		Data d = new Data(Double.class, result);
		d.setLabel(label);
		return d;
	}

	@Override
	public Data getPreviousValue() {
		return new Data( Boolean.class ,lastValue);
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
