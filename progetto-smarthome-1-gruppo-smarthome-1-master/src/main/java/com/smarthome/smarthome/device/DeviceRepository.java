package com.smarthome.smarthome.device;

import com.smarthome.smarthome.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>
{
    @Query("SELECT d FROM Device d WHERE d.label = ?1")
    Optional<Device> findDeviceByLabel(String label);

    Optional<List<Device>> findDevicesByRoom(Room room);
}
