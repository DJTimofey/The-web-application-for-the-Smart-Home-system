package com.smarthome.smarthome.agent.strategy;

import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;

public interface Strategy
{
	public abstract void execute(Rilevation rilevazione, DeviceService deviceService);
}
