package com.smarthome.smarthome.application;

import com.smarthome.smarthome.agent.AgentiStatus;
import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.RilevationService;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class AlarmController extends Controller{

    @Autowired
    protected AlarmController(DeviceService deviceService, RilevationService rilevationService)
    {
        super(deviceService, rilevationService);
    }

    
    @PostMapping(path="/set-alarm")
    public void receiveSensorData(@RequestBody JSONObject jsonData)
    {
        String data = (String) jsonData.get("value");
        AgentiStatus.setAllarme(data.equals("ON"));

    }

    @GetMapping(path="/get-alarm", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getAlarm(){

        JSONObject jo=new JSONObject();
        boolean alarm=AgentiStatus.getAllarme();
        if(alarm)
            jo.put("alarm", "ON");
        else
            jo.put("alarm", "OFF");

        return new ResponseEntity<>(jo, HttpStatus.OK);
    }
}
