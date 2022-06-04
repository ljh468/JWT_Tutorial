package com.jwttutorial.jwttutorial.config;

import com.jwttutorial.jwttutorial.jwt.JwtAccessDeniedHandler;
import com.jwttutorial.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import com.jwttutorial.jwttutorial.jwt.JwtSecurityConfig;
import com.jwttutorial.jwttutorial.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
// preAuthorize 어노테이션을 메서드 단위로 추가하기 위해서 적용
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    public SecurityConfig(  //생성자 주입
                            TokenProvider tokenProvider,
                            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web){
        web
                // h2-console 하위요청과 favicon관련 요청은 시큐리티 모두 무시
                .ignoring()
                .antMatchers(
                        "/h2-console/**",
                        "/favicon.ico"
                );
    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 토큰방식을 사용하기 때문에 csrf방식은 사용안함
                .csrf().disable()
                // 예외를 handling할때 구현한 클래스를 추가해줌
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // h2-console을 위한 설정을 추가
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // HttpServletRequest 요청에 접근제한을 설정
                .and()
                .authorizeRequests()
                // "/api/hello" 요청허용
                .antMatchers("/api/hello").permitAll()
                // 토큰을 받기위한 로그인 API와 회원가입 API는 토큰이 없는 상태에서 요청이 들어오기 때문에 모두 허가
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/signup").permitAll()
                // 나머지는 모두 인증을 받아야함
                .anyRequest().authenticated()

                // JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스도 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
