package com.example.memberservice.entity;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "`patient`")
public class Patient extends BaseEntityDeletedAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	private String sick;

	public void updatePatient(String sick) {
		this.sick = sick;
	}
}
