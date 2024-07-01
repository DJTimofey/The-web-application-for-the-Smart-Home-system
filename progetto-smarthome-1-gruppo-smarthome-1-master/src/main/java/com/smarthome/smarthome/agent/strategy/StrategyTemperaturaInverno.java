package com.smarthome.smarthome.agent.strategy;

import java.util.List;

import com.smarthome.smarthome.agent.AgenteTemperatura;
import com.smarthome.smarthome.device.Actuator;
import com.smarthome.smarthome.device.Category;
import com.smarthome.smarthome.device.Device;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.Rilevation;

public class StrategyTemperaturaInverno implements Strategy
{
	@Override
	public void execute(Rilevation rilevazione, DeviceService deviceService)
	{
		double target = AgenteTemperatura.getTemperatura();
		double temperatura = rilevazione.getValue();
		Device sensor = rilevazione.getDevice();
		List<Device> devices = deviceService.getDeviceByRoom(sensor.getRoom());
		for(Device device : devices)
		{
				if(device.getCategory() == Category.TERMOSIFONE)
				{
					Actuator termosifone = new Actuator(device.getId(), device.getLabel(), device.getCategory(), device.getRoom());
					String state = termosifone.getCurrentState();
					boolean accendere = (state.equals("OFF") || state.equals("Spegnimento")) && temperatura < target;
					boolean spegnere = (state.equals("ON") || state.equals("Accensione")) && temperatura > target;
					if(accendere || spegnere)
						termosifone.controlSignal();
				}
		}
	}
}
