package com.smarthome.smarthome.device;

import com.smarthome.smarthome.room.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DeviceService
{
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getDeviceByRoom(Room room)
    {
        return deviceRepository.findDevicesByRoom(room).orElse(Collections.emptyList());
    }

    public Device getDeviceByLabel(String label)
    {
        Device d = deviceRepository.findDeviceByLabel(label).orElse(null);

        if (d != null)
        {
            if (d.isDeviceType())
                return new Actuator(d.getId(), d.getLabel(), d.getCategory(), d.getRoom());
            else
                return new Sensor(d.getId(), d.getLabel(), d.getCategory(), d.getRoom());
        }
        else
            return null;
    }

    public List<Device> getAllDevices(){
        return deviceRepository.findAll();
    }

    public void deleteDevice(Long deviceId)
    {
        boolean exists = deviceRepository.existsById(deviceId);

        if (!exists)
            throw new IllegalStateException("Impossibile eliminare il device. Il device non esiste");

        deviceRepository.deleteById(deviceId);
    }

    public Device getDeviceById(Long deviceId){
        return deviceRepository.getById(deviceId);
    }
}

