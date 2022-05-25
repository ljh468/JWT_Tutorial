package com.jwttutorial.jwttutorial.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil(){
    }

    /**
     * Authentication 객체를 꺼내와서 username을 반환
     * (JwtFilter의 doFilter가 실행될때 Authentication 객체가 채워짐)
     * @return
     */
    public static Optional<String> getCurrentUsername(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username =null;
        if(authentication.getPrincipal() instanceof UserDetails){
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if(authentication.getPrincipal() instanceof String){
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}
