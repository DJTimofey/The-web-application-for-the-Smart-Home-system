package com.smarthome.smarthome.application;

import com.smarthome.smarthome.device.DeviceService;
import com.smarthome.smarthome.rilevation.RilevationService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class Controller
{
    DeviceService deviceService;
    RilevationService rilevationService;

    @Autowired
    protected Controller(DeviceService deviceService, RilevationService rilevationService)
    {
        this.deviceService = deviceService;
        this.rilevationService = rilevationService;
    }

    public abstract void receiveSensorData(@RequestBody JSONObject jsonData);
}
