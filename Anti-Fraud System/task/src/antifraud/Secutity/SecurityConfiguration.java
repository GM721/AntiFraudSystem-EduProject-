package antifraud.Secutity;

import antifraud.Exception.RestAuthenticationEntryPoint;
import antifraud.Enums.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // Handles auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, the H2 console
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET,     "/api/auth/list").hasAnyAuthority(Roles.ADMINISTRATOR.getRole(), Roles.SUPPORT.getRole())
                .mvcMatchers(HttpMethod.GET,     "/api/antifraud/history").hasAnyAuthority(Roles.SUPPORT.getRole())
                .mvcMatchers(HttpMethod.GET,   "/api/antifraud/history/*").hasAnyAuthority(Roles.SUPPORT.getRole())
                .mvcMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAnyAuthority(Roles.SUPPORT.getRole())
                .mvcMatchers(HttpMethod.POST,"/api/antifraud/transaction").hasAuthority(Roles.MERCHANT.getRole())
                .mvcMatchers(HttpMethod.PUT,     "/api/auth/role").hasAnyAuthority(Roles.ADMINISTRATOR.getRole())
                .mvcMatchers(HttpMethod.PUT,   "/api/auth/access").hasAnyAuthority(Roles.ADMINISTRATOR.getRole())
                .mvcMatchers(HttpMethod.DELETE,"/api/auth/user/*").hasAnyAuthority(Roles.ADMINISTRATOR.getRole())
                .mvcMatchers(         "api/antifraud/suspicious-ip").hasAnyAuthority(Roles.SUPPORT.getRole())
                .mvcMatchers(            "api/antifraud/stolencard").hasAnyAuthority(Roles.SUPPORT.getRole())
                .mvcMatchers(       "api/antifraud/suspicious-ip/*").hasAnyAuthority(Roles.SUPPORT.getRole())
                .mvcMatchers(          "api/antifraud/stolencard/*").hasAnyAuthority(Roles.SUPPORT.getRole())
                .antMatchers(HttpMethod.POST,    "/api/auth/user").permitAll()
                .antMatchers("/actuator/shutdown").permitAll()// manage access
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // no session
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsServiceImpl) // user store 1
                .passwordEncoder(getEncoder());

    }
}
