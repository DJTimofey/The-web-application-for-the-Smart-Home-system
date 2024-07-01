package com.smarthome.smarthome.emergenza;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.smarthome.smarthome.room.Room;

@Repository
public interface EmergenzaRepository extends JpaRepository<Emergenza, Long>{
	
	@Query("SELECT e FROM Emergenza e WHERE e.id = ?1")
	Optional<Emergenza> findEmergenzaById(long id);
	
	Optional<List<Emergenza>> findEmergenzaByRoom(Room room);
	
    @Query(value="COUNT(*) FROM Emergenza", nativeQuery=true)
    int countRilevation();

    @Query(value="COUNT(*) FROM Emergenza e WHERE e.id=?1", nativeQuery=true)
    int countRilevationById(long id);

    @Query("SELECT e FROM Emergenza e WHERE e.isEmergencyRead = false")
    List<Emergenza> getPending();

    @Modifying
    @Query("update Emergenza e set e.isEmergencyRead=?1 where e.id = ?2")
    void updateStatus( boolean newStatus, long emergenzaId);
}
