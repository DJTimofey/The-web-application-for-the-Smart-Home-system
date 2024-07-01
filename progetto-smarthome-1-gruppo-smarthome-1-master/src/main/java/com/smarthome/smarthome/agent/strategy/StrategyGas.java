package com.smarthome.smarthome.agent.strategy;

import java.util.List;

import com.smarthome.smarthome.device.Actuator;
import com.smarthome.smarthome.device.Category;
import com.smarthome.smarthome.device.Device;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;
import com.smarthome.smarthome.agent.AgentiStatus;

public class StrategyGas implements Strategy
{
	@Override
	public void execute(Rilevation rilevazione, DeviceService deviceService)
	{
		boolean signal = rilevazione.getValue() == 1.0;
		if(signal)
		{
			AgentiStatus.setTemperatura(!signal);
			Device sensor = rilevazione.getDevice();
			List<Device> devices = deviceService.getDeviceByRoom(sensor.getRoom());

			for(Device device : devices)
				if(device.getCategory() == Category.FINESTRA)
				{
					Actuator finestra = new Actuator(device.getId(), device.getLabel(), device.getCategory(), device.getRoom());
					String state = finestra.getCurrentState();

					if(state.equals("Chiusa") || state.equals("Chiusura"))
						finestra.controlSignal();
				}
		}
	}
}
