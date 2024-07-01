package com.smarthome.smarthome.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.smarthome.smarthome.device.*;
import com.smarthome.smarthome.rilevation.Rilevation;
import com.smarthome.smarthome.rilevation.RilevationService;
import com.smarthome.smarthome.agent.Agente;
import com.smarthome.smarthome.agent.AgenteTemperatura;
import com.smarthome.smarthome.agent.AgentiStatus;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class ThermometerController extends Controller
{
    @Autowired
    public ThermometerController(DeviceService deviceService, RilevationService rilevationService)
    {
        super(deviceService, rilevationService);
    }

    @PostMapping("/thermometer")
    public void receiveSensorData(@RequestBody JSONObject jsonData)
    {
    	Rilevation rilevation = new Rilevation(jsonData, deviceService, rilevationService);

        if(AgentiStatus.getTemperatura())
        {
    		Agente agente = new AgenteTemperatura(rilevation, deviceService);
    		agente.run();
    	}
    }
    
    @PostMapping(path= "/set-temperature", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setTemperature(@RequestBody JSONObject jsonData) {

        if(AgentiStatus.getTemperatura())
        {
            Integer temperatura= (Integer) jsonData.get("temperature");
            Double value = Double.valueOf(temperatura);
            AgenteTemperatura.setTemperatura(value);
        }
        

        return new ResponseEntity<>("Temperatura impostata con successo", HttpStatus.OK);
    }
    
    
}
