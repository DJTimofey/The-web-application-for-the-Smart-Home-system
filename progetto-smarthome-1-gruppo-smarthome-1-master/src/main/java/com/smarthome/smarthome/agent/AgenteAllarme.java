package com.smarthome.smarthome.agent;

import com.smarthome.smarthome.agent.strategy.Strategy;
import com.smarthome.smarthome.agent.strategy.StrategyAllarme;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;

public class AgenteAllarme extends Agente
{	
	public AgenteAllarme(Rilevation rilevazione, DeviceService deviceService)
	{
		super(rilevazione, deviceService);
	}

	@Override
	public void run()
	{
		Strategy strategy = new StrategyAllarme();
		strategy.execute(this.rilevazione, this.deviceService);
	}
}