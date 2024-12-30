package com.example.logincustom.Config;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//맵핑별 권한, 로그인, 로그아웃, csrf(페이지 보안)
//다중로그인
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //비밀번호에 대한 암호화 변환방식
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        }


    //맵핑별 사용권한 부여(Controller)
    //antMatchers:이전버전
    //permitAll : 모든 사용자 이용 가능
    //hasRole("권한") : 해당 권한자만 이용 가능
    //hasAnyRole("권한1", "권한2") : 해당 권한자들만 이용 가능
    //authenticated : 로그인을 성공한 사용자만 이용 가능
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth)->{
                auth.requestMatchers("/").permitAll();
                auth.requestMatchers("/login", "/logout", "register").permitAll();
                auth.requestMatchers("/user").hasAnyRole("USER", "ADMIN");
                auth.requestMatchers("/admin").hasAnyRole("ADMIN");
                auth.requestMatchers("/result").authenticated();
                auth.requestMatchers("/h2-console/**").permitAll();
        });

        //로그인폼에 대한 정보
        //defaultSuccessUrl() : 로그인 성공시 이동할 페이지
        //loginPage : 기존 로그인페이지가 아닌 사용자가 만든 로그인페이지를 이용할 때 맵핑
        //username, password 통해서 아이디와 비밀번호를 받는다.
        //usernameParameter : username을 대체할 input name명
        http.formLogin(login->login
                .defaultSuccessUrl("/", true) // 로그인 성공시 메인페이지 이동
                .loginPage("/login") //로그인폼은 사용자가 작성한 /login으로 이동
                .usernameParameter("loginid") //userid를 username에 적용
                .permitAll()
        );

        //페이지 변조방지(HTML에 선언해서 사용)
        http.csrf(AbstractHttpConfigurer::disable);

        //로그아웃 처리시 작업
        //logoutUrl : 기존 로그아웃이 사용자 로그아웃처리할 맵핑
        //logoutSuccessUrl : 성공적으로 로그아웃 처리 후 이동할 페이지 맵핑
        http.logout(logout->logout
                .logoutUrl("/logout") //로그아웃 맵핑명
                .logoutSuccessUrl("/login") //로그아웃 성공시 로그인페이지로 이동
        );
        return http.build();
    }
}
