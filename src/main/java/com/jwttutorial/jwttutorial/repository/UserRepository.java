package com.jwttutorial.jwttutorial.repository;

import com.jwttutorial.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "authorities")
    // Username을 기준으로해서 권한정보와 유저정보를 함께 가져옴
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
