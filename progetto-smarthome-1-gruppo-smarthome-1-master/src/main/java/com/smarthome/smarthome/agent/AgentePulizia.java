package com.smarthome.smarthome.agent;



import java.util.TimerTask;

import com.smarthome.smarthome.device.Actuator;

public class AgentePulizia extends TimerTask
{	
	private Actuator puliziaBot;
	private long delay;
	private long interval;
	
	public AgentePulizia(Actuator puliziaBot, long delay, long interval)
	{
		this.puliziaBot = puliziaBot;
		this.delay = delay;
		this.interval = interval;
	}

	@Override
	public void run()
	{
		if(puliziaBot.getCurrentState().equals("In Carica"))
			puliziaBot.controlSignal();
	}
	
	public long getDelay(){
		return this.delay;
	}
	
	public long getInterval(){
		return this.interval;
	}
	
}
