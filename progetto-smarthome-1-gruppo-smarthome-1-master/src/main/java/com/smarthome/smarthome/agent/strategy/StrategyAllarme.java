package com.smarthome.smarthome.agent.strategy;

import java.util.List;

import com.smarthome.smarthome.device.Actuator;
import com.smarthome.smarthome.device.Category;
import com.smarthome.smarthome.device.Device;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;
import com.smarthome.smarthome.agent.AgentiStatus;

public class StrategyAllarme implements Strategy
{
	@Override
	public void execute(Rilevation rilevazione, DeviceService deviceService)
	{
		boolean signal = rilevazione.getValue() == 1.0;

		if(signal)
		{
			AgentiStatus.setAllarme(signal);
			List<Device> devices = deviceService.getAllDevices();
			for(Device device : devices) {
				if(device.getCategory() == Category.SIRENA)
				{
					Actuator sirena = new Actuator(device.getId(), device.getLabel(), device.getCategory(), device.getRoom());
					String state = sirena.getCurrentState();

					if(state.equals("OFF") || state.equals("Spegnimento"))
						sirena.controlSignal();
				}
			}
		}
	}
}
