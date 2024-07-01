package com.smarthome.smarthome.rilevation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smarthome.smarthome.device.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface RilevationRepository extends JpaRepository<Rilevation, Long>
{
    @Query("SELECT r FROM Rilevation r WHERE r.id = ?1")
    Optional<Rilevation> findRilevationById(Long id);
    
    //@Query("SELECT * FROM rilevation r JOIN device d ON r. = d. WHERE d.id=")
    Optional<List<Rilevation>> findRilevationByDevice(Device device);

    @Query(value="COUNT(*) FROM Rilevation", nativeQuery=true)
    int countRilevation();

    @Query(value="COUNT(*) FROM rilevation r WHERE r.id=?1", nativeQuery=true)
    int countRilevationById(long id);

    @Query(value="SELECT * FROM rilevation join device ON device_id = device.id WHERE device.category='TERMOMETRO' ORDER BY rilevation.timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<Rilevation> findLastTemperature();
}
