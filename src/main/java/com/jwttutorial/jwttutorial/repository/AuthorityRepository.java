package com.jwttutorial.jwttutorial.repository;

import com.jwttutorial.jwttutorial.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

}
