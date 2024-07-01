package com.smarthome.smarthome.agent;

import java.util.Calendar;

import com.smarthome.smarthome.agent.strategy.Strategy;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;
import com.smarthome.smarthome.agent.strategy.StrategyTemperaturaEstate;
import com.smarthome.smarthome.agent.strategy.StrategyTemperaturaInverno;

public class AgenteTemperatura extends Agente
{
	private static double temperatura;

	public AgenteTemperatura(Rilevation rilevazione, DeviceService deviceService)
	{
		super(rilevazione, deviceService);
	}

	@Override
	public void run()
	{
		Strategy strategy;
		int month = Calendar.MONTH;

		if( 5 <= month && month <= 9)
			strategy = new StrategyTemperaturaEstate();
		else
			strategy = new StrategyTemperaturaInverno();

		strategy.execute(rilevazione, deviceService);
	}

	public static double getTemperatura()
	{
		return temperatura;
	}

	public static void setTemperatura(double temperatura)
	{
		AgenteTemperatura.temperatura = temperatura;
	}
}