package com.smarthome.smarthome.emergenza;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smarthome.smarthome.room.Room;

@Service
public class EmergenzaService
{
	private final EmergenzaRepository emergenzaRepository;
	
	@Autowired
	public EmergenzaService(EmergenzaRepository emergenzaRepository) {
		this.emergenzaRepository = emergenzaRepository;
	}
	
	public List<Emergenza> getEmergenzaByRoom(Room room)
    {
		return emergenzaRepository.findEmergenzaByRoom(room).orElse(Collections.emptyList());
	}
	
    public Emergenza getEmergenzaById(Long id)
    {
        Emergenza e = emergenzaRepository.findEmergenzaById(id).orElse(null);

        if (e != null)
           return new Emergenza(e.getId(), e.getCode(), e.getTimeStamp(), e.getRoom());
        else
            return null;
    }
	
    public void saveEmergenza(Emergenza emergenza){
        emergenzaRepository.save(emergenza);
    }

    public List<Emergenza> getAllEmergenze(){return emergenzaRepository.findAll();}

    public List<Emergenza> getPendingEmergenze() {
        return emergenzaRepository.getPending();
    }

    public void updateEmergenzaStatus(long emergenzaId, boolean newStatus)
    {
        Emergenza e = emergenzaRepository.getById(emergenzaId);

        e.setEmergencyRead(newStatus);
        emergenzaRepository.deleteById(emergenzaId);

        emergenzaRepository.save(e);
    }

}
