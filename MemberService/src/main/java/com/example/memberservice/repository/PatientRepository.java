package com.example.memberservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.memberservice.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
