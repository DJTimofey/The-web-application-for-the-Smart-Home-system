package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByRoomId(Long roomId);
    List<Device> findByUser(User user);
    List<Device> findByRoomIdAndUser(Long roomId, User user);

    Device findByName(String yourDeviceName);

    List<Device> findByType(String type);

    Device findTopByOrderByIdDesc();
    Device findTopByTypeOrderByIdDesc(String type);
    List<Device> findAllByType(String type);

    @Query("SELECT u.address AS address, u.phone AS phone, u.username AS username FROM User u JOIN Device d ON u.id = d.user.id WHERE d.id = :deviceId")
    Map<String, String> findUserDataByDeviceId(@Param("deviceId") Long deviceId);


    List<Device> findByUserAndType(User user, String type);
    List<Device> findByUser_Id(Long userId);

}