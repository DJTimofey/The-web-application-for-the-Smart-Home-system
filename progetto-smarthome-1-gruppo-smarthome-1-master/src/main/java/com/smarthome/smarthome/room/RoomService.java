package com.smarthome.smarthome.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService
{
    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {this.roomRepository = roomRepository;}

    public List<Room> getRooms(){return roomRepository.findAll();}

    public void addNewRoom(Room room)
    {
        Optional<Room> roomOptional = roomRepository.findRoomByName(room.getName());

        if (roomOptional.isPresent())
            throw new IllegalStateException("Impossibile aggiungere stanza. Stanza già presente.");

        roomRepository.save(room);
    }

    public void deleteRoom(Long roomId)
    {
        boolean exists = roomRepository.existsById(roomId);

        if (!exists)
            throw new IllegalStateException("Impossibile eliminare la stanza. La stanza non esiste.");

        roomRepository.deleteById(roomId);
    }
}
