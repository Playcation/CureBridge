package com.example.memberservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.memberservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	// TODO: 커스텀 예외 적용 필요
	default User findByIdOrElseThrow(Long id) {
		User user = findById(id).orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
		if(user.getDeletedAt() != null) {
			throw new RuntimeException("삭제된 유저입니다.");
		}
		return user;
	}
}
