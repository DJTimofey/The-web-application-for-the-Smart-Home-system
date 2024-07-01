package com.smarthome.smarthome.emergenza;

import java.sql.Timestamp;

import javax.persistence.*;

import com.smarthome.smarthome.room.Room;


@Entity
@Table(name = "Emergenza")
public class Emergenza
{
	@Id
	@SequenceGenerator(
			name="emergency_sequence",
			sequenceName="emergency_sequence",
			allocationSize = 1
	)
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "emergency_sequence"
	)
	private long id;
	private Timestamp timeStamp;
	
	@Enumerated(EnumType.STRING)
	private EmergencyCode code;
    
	@ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

	private boolean isEmergencyRead;

	public Emergenza(EmergencyCode code, Timestamp timeStamp, Room r)
	{
		this.code = code;
		this.timeStamp = timeStamp;
		this.room = r;
		this.isEmergencyRead = false;
	}
	
	public Emergenza(long id, EmergencyCode code, Timestamp timeStamp, Room r)
	{
		this.code = code;
		this.timeStamp = timeStamp;
		this.room = r;
		this.id = id;
		this.isEmergencyRead = false;
	}

	public Emergenza() {}

	public Timestamp getTimeStamp() {
    	return this.timeStamp;
    }
    
    public EmergencyCode getCode() {
		return this.code;
	}
	
	public Room getRoom() {
		return this.room;
	}
	
	public long getId() {
		return this.id;
	}

	public boolean isEmergencyRead() {
		return isEmergencyRead;
	}

	public void setEmergencyRead(boolean emergencyRead) {
		isEmergencyRead = emergencyRead;
	}

}
