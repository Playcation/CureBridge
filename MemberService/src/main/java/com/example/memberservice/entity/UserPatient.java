package com.example.memberservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "`user_patient`")
public class UserPatient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// User 연결 (User)
	private Long user_id;

	// Patient 연걸 (User)
	private Long patient_id;
}
