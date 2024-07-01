package com.smarthome.smarthome.rilevation;

import com.smarthome.smarthome.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class RilevationService
{
    private final RilevationRepository rilevationRepository;

    @Autowired
    public RilevationService(RilevationRepository rilevationRepository)
    {
        this.rilevationRepository = rilevationRepository;
    }
    
    public List<Rilevation> getRilevationByDevice(Device device)
    {
        return rilevationRepository.findRilevationByDevice(device).orElse(Collections.emptyList());
    }

    public Rilevation getRilevationById(Long id)
    {
        Rilevation r = rilevationRepository.findRilevationById(id).orElse(null);

        if (r != null)
           return new Rilevation(r.getId(), r.getTimestamp(), r.getValue(), r.getValueType(), r.getDevice());
        else
            return null;
    }

    public Rilevation saveRilevation(Rilevation rilevation){
        return rilevationRepository.save(rilevation);
    }

    public Rilevation getLastTemperaturRilevation()
    {
        Rilevation r = rilevationRepository.findLastTemperature().orElse(null);

        if (r != null)
           return r;
        else
            return null;
    }
}
