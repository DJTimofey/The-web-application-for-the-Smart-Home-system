package com.smarthome.smarthome.agent;

import com.smarthome.smarthome.agent.strategy.Strategy;
import com.smarthome.smarthome.agent.strategy.StrategyGas;
import com.smarthome.smarthome.agent.strategy.StrategySmoke;
import com.smarthome.smarthome.device.Category;
import com.smarthome.smarthome.device.Device;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;

public class AgentePericoli extends Agente
{
	public AgentePericoli(Rilevation rilevazione, DeviceService deviceService)
	{
		super(rilevazione, deviceService);
	}

	@Override
	public void run()
	{
		Strategy strategy;
		Device sensor = rilevazione.getDevice();

		if(sensor.getCategory() == Category.GAS)
			strategy = new StrategyGas();
		else
			strategy = new StrategySmoke();

		strategy.execute(rilevazione, deviceService);
	}
}