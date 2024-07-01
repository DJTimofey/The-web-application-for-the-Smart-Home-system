package com.smarthome.smarthome.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>
{
    @Query("SELECT r FROM Room r WHERE r.name = ?1")
    Optional<Room> findRoomByName(String name);

    @Query(value="SELECT * FROM room LEFT JOIN device ON room.id=room_id LEFT JOIN (SELECT * FROM rilevation ORDER BY rilevation.id DESC LIMIT 1) t1 ON device.id = t1.device_id", nativeQuery = true)
    List<Room> findRoomInfo();
}
