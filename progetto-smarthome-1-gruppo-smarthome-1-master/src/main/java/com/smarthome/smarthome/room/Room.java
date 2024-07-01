package com.smarthome.smarthome.room;

import java.util.Set;

import javax.persistence.*;

import com.smarthome.smarthome.device.Device;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

@Entity
@Table(name = "room")
public class Room
{
    @Id
    @SequenceGenerator(
            name = "room_sequence",
            sequenceName = "room_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "room_sequence"
    )
    private Long id;

    @Column(unique=true)
    private String name;

    @OneToMany(mappedBy="room") 
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Set<Device> devices;
    

    public Room(){}

    public Room(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Room(Long id, String name, Set<Device> devices)
    {
        this.id = id;
        this.name = name;
        this.devices=devices;
    }

    public Room( String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDevices(Set<Device> devices){
        this.devices=devices;
    }

    public Set<Device> getDevices(){
        return devices;
    }

    @Override
    public String toString()
    {
        JSONObject jo=new JSONObject();
        
        jo.put("room", new JSONObject()
            .put("id", id)
            .put("name", name)
            .put("devices", devices.toString()));
        
        return jo.toString();
    }
}