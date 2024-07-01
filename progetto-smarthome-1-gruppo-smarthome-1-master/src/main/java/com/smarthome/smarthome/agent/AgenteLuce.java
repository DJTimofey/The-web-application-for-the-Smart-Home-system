package com.smarthome.smarthome.agent;

import java.time.LocalTime;

import com.smarthome.smarthome.agent.strategy.Strategy;
import com.smarthome.smarthome.agent.strategy.StrategyLuceGiorno;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;

public class AgenteLuce extends Agente
{
	public AgenteLuce(Rilevation rilevazione, DeviceService deviceService)
	{
		super(rilevazione, deviceService);
	}

	@Override
	public void run()
	{
		int localTime = LocalTime.now().getHour();
		Strategy strategy;

		if( localTime >= 7 && localTime <= 23)
		{
			strategy = new StrategyLuceGiorno();
			strategy.execute(this.rilevazione, this.deviceService);
		}
	}
}
