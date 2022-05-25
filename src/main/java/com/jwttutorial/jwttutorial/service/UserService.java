package com.jwttutorial.jwttutorial.service;

import com.jwttutorial.jwttutorial.dto.UserDto;
import com.jwttutorial.jwttutorial.entity.Authority;
import com.jwttutorial.jwttutorial.entity.User;
import com.jwttutorial.jwttutorial.repository.UserRepository;
import com.jwttutorial.jwttutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 유저 회원가입 메서드
     * @param userDto
     * @return
     */
    @Transactional
    public User signup(UserDto userDto) {
        // username을 조회하여 중복확인
        if(userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null){
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 유저정보가 없으면 권한정보를 만듬 (권한은 ROLE_USER 하나만 가진다.)
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // 권한정보와 유저정보를 담아서 저장
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * username을 기준으로 유저정보(User)를 가져옴
     * @param username
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    /**
     * SecurityContext에 저장된 유저정보만 가져옴
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}
