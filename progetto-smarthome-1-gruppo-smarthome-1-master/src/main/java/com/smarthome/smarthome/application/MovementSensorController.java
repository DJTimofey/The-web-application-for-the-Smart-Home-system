package com.smarthome.smarthome.application;

import java.util.Calendar;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.smarthome.smarthome.device.Device;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.emergenza.EmergencyCode;
import com.smarthome.smarthome.emergenza.Emergenza;
import com.smarthome.smarthome.emergenza.EmergenzaRepository;
import com.smarthome.smarthome.rilevation.Rilevation;
import com.smarthome.smarthome.rilevation.RilevationService;
import com.smarthome.smarthome.agent.Agente;
import com.smarthome.smarthome.agent.AgenteAllarme;
import com.smarthome.smarthome.agent.AgenteLuce;
import com.smarthome.smarthome.agent.AgentiStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class MovementSensorController extends Controller
{
	EmergenzaRepository emergenzaRepo;
    
	@Autowired
    public MovementSensorController(DeviceService deviceService, RilevationService rilevationService, EmergenzaRepository emergenzaRepo)
    {
        super(deviceService, rilevationService);
        this.emergenzaRepo = emergenzaRepo;
    }
    
    @PostMapping("/movement")
    public void receiveSensorData(@RequestBody JSONObject jsonData)
    {
    	Rilevation rilevation = new Rilevation(jsonData, deviceService, rilevationService);

    	if(AgentiStatus.getAllarme() && rilevation.getValue() == 1.0)
        {
    		Agente agente = new AgenteAllarme(rilevation, deviceService);
    		agente.run();
    		Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
            Device sensor = rilevation.getDevice();
    	    Emergenza e = new Emergenza(EmergencyCode.INTRUSIONE, currentTimestamp, sensor.getRoom());
    	    emergenzaRepo.save(e);
    	}
        else
        {
    		Agente agente = new AgenteLuce(rilevation, deviceService);
    		agente.run();
    	}
    }
    
    
}

