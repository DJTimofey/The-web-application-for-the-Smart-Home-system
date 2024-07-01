package com.example.smarthomeapp.repository;


import com.example.smarthomeapp.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findAllByUser_Id(Long userId);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.user.id = :userId")
    List<ServiceRequest> findByUserId(@Param("userId") Long userId);

    List<ServiceRequest> findByRole(String admin);
}
