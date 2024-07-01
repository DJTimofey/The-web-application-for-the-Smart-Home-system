package com.smarthome.smarthome.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/device")
public class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService){
        this.deviceService = deviceService;
    }

    @GetMapping(path="{deviceId}")
    public ResponseEntity<String> getDeviceValue(@PathVariable("deviceId") Long deviceId)
    {
        Device d = deviceService.getDeviceById(deviceId);

        if (d.isDeviceType())
        {
            Actuator a = new Actuator(d.getId(), d.getLabel(), d.getCategory(), d.getRoom());
            return new ResponseEntity<>(a.getCurrentState(), HttpStatus.OK);
        }
        else
        {
            Sensor s = new Sensor(d.getId(), d.getLabel(), d.getCategory(), d.getRoom());
            return new ResponseEntity<>(String.valueOf(s.getDataFeed()), HttpStatus.OK);
        }
    }

    @DeleteMapping(path="{deviceId}")
    public ResponseEntity<String> deleteDevice(@PathVariable("deviceId") Long deviceId){

        try
        {
            deviceService.deleteDevice(deviceId);
            return new ResponseEntity<>("Device rimosso con successo", HttpStatus.CREATED);
        }
        catch(IllegalStateException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
