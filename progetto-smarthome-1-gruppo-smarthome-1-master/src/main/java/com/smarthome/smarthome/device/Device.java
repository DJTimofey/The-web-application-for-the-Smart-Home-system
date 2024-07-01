package com.smarthome.smarthome.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smarthome.smarthome.rilevation.Rilevation;
import com.smarthome.smarthome.room.Room;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "device")
public class Device
{
    @Id
    @SequenceGenerator(
            name="device_sequence",
            sequenceName="device_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "device_sequence"
    )
    private Long id;
    private String label;
    private boolean deviceType;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(optional = true)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.NO_ACTION)    
    public Room room;

    @OneToMany(mappedBy="device")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Set<Rilevation> rilevations;


    public Device(){}

    public Device(Long id, String label, Category category, Room room, boolean deviceType)
    {
        this.id = id;
        this.label = label;
        this.category = category;
        this.room = room;
        this.deviceType = deviceType;
        this.rilevations = new HashSet<>();
    }

    public Device(String label, Category category, Room room, boolean deviceType)
    {
        this.label = label;
        this.category = category;
        this.room = room;
        this.deviceType = deviceType;
        this.rilevations = new HashSet<>();
    }

    public Device(String label, Category category, Room room, boolean deviceType, Set<Rilevation> rilevations)
    {
        this.label = label;
        this.category = category;
        this.room = room;
        this.deviceType = deviceType;
        this.rilevations=rilevations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isDeviceType() {
        return deviceType;
    }

    public void setDeviceType(boolean tipo) {
        this.deviceType = tipo;
    }

    public void setRilevations(Set<Rilevation> rilevations){
        this.rilevations=rilevations;
    }

    public Set<Rilevation> getRilevations(){
        return rilevations;
    }

    @Override
    public String toString()
    {
        JSONObject jo=new JSONObject();
        jo.put("device", new JSONObject()
            .put("id", id)
            .put("label", label)
            .put("category", category)
            .put("deviceType", deviceType));

        if(rilevations != null)
            jo.put("rilevations", rilevations.toString());

        return jo.toString();
    }
}
