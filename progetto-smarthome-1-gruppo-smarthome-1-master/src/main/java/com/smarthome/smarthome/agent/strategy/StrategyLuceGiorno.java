package com.smarthome.smarthome.agent.strategy;

import java.util.List;

import com.smarthome.smarthome.device.Actuator;
import com.smarthome.smarthome.device.Category;
import com.smarthome.smarthome.device.Device;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;

public class StrategyLuceGiorno implements Strategy
{
	@Override
	public void execute(Rilevation rilevazione, DeviceService deviceService)
	{
		Device sensor = rilevazione.getDevice();
		List<Device> devices = deviceService.getDeviceByRoom(sensor.getRoom());

		for(Device device : devices)
			if(device.getCategory() == Category.LAMPADA && device.isDeviceType())
			{
				Actuator lampada = new Actuator(device.getId(), device.getLabel(), device.getCategory(), device.getRoom());
				String state = lampada.getCurrentState();

				if(state.equals("OFF") || state.equals("Spegnimento"))
				{
					if(rilevazione.getValue() == 1.0)
						lampada.controlSignal();
				}
				else
					if(rilevazione.getValue() == 0.0)
						lampada.controlSignal();
			}
	}
}
