package com.smarthome.smarthome.agent.strategy;

import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;
import com.smarthome.smarthome.agent.AgentiStatus;

public class StrategySmoke implements Strategy
{
	@Override
	public void execute(Rilevation rilevazione, DeviceService deviceService)
	{
		boolean signal = rilevazione.getValue() == 1.0;

		if(signal)
			AgentiStatus.setTemperatura(!signal);
	}
}
