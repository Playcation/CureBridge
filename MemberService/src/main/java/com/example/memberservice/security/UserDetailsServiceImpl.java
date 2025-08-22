package com.example.memberservice.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.memberservice.entity.User;
import com.example.memberservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("username을 찾을 수 없습니다."));   // 사용자가 DB 에 없으면 예외처리

		return new UserDetailsImpl(user, user.getEmail());
	}
}
