package com.smarthome.smarthome.room;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/rooms")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Room>> getAllRooms()
    {
        List<Room> ld = roomService.getRooms();

        return new ResponseEntity<>(ld, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> registerNewRoom(@RequestBody JSONObject jsonData)
    {
        Room room = new Room((String) jsonData.get("name"));
        try
        {
            roomService.addNewRoom(room);
            return new ResponseEntity<>("Stanza aggiunta", HttpStatus.OK);
        }
        catch (IllegalStateException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping(path = "{roomId}")
    public ResponseEntity<Object> deleteRoom(@PathVariable("roomId") Long roomId)
    {
        try
        {
            roomService.deleteRoom(roomId);
            return new ResponseEntity<>("Stanza rimossa", HttpStatus.CREATED);
        }
        catch (IllegalStateException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
