package com.smarthome.smarthome.agent;

import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;

public abstract class Agente
{
	protected Rilevation rilevazione;
	protected DeviceService deviceService;
	
	protected Agente(Rilevation rilevazione, DeviceService deviceService)
	{
		this.rilevazione = rilevazione;
		this.deviceService = deviceService;
	}
	
	public abstract void run();
}
