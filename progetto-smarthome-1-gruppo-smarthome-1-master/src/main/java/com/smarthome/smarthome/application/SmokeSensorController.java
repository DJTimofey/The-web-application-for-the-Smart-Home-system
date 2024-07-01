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
import com.smarthome.smarthome.agent.AgentePericoli;
import com.smarthome.smarthome.agent.AgentiStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class SmokeSensorController extends Controller
{
    private EmergenzaRepository emergenzaRepo;

	@Autowired
    public SmokeSensorController(DeviceService deviceService, RilevationService rilevationService, EmergenzaRepository emergenzaRepo)
    {
        super(deviceService, rilevationService);
        this.emergenzaRepo = emergenzaRepo;
    }
    
    @PostMapping("/smokeSensor")
    public void receiveSensorData(@RequestBody JSONObject jsonData)
    {
    	Rilevation rilevation = new Rilevation(jsonData, deviceService, rilevationService);

    	if(AgentiStatus.getPericoli() && rilevation.getValue() == 1.0)
        {	
    		Calendar calendar = Calendar.getInstance();
            java.util.Date now = calendar.getTime();
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
            Device sensor = rilevation.getDevice();
    	    Emergenza e = new Emergenza(EmergencyCode.FUMO, currentTimestamp, sensor.getRoom());
    	    emergenzaRepo.save(e);
    		Agente agente = new AgentePericoli(rilevation, deviceService);
    		agente.run();
    	}
    }    
}