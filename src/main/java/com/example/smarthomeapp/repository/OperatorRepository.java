package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {
    Operator findByUsername(String username);
}