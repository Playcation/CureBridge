package com.example.memberservice.entity;

import java.util.Date;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;
import com.example.memberservice.dto.UserResponseDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "`member`")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntityDeletedAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;

	private String password;

	private String name;

	@Enumerated(value = EnumType.STRING)
	private String role;

	private String phoneNumber;

	private Date birth;

	public User(String email, String password, String name, String role, String phoneNumber, Date birth) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.role = role;
		this.phoneNumber = phoneNumber;
		this.birth = birth;
	}

	// TODO: sick 부분 기본값 정하기
	public UserResponseDto toDto() {
		return new UserResponseDto(
			this.name,
			this.email,
			this.birth,
			"없음",
			this.getCreatedAt(),
			this.getUpdatedAt()
		);
	}

	public void updatePassword(String encodedPassword) {
		this.password = encodedPassword;
	}
}
