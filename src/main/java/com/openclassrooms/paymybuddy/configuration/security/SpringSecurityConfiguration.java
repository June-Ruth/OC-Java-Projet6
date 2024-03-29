package com.openclassrooms.paymybuddy.configuration.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /**
     * @see UserDetailsService
     */
    private final UserDetailsService userDetailsService;

    /**
     * Autowired constructor.
     * @param pUserDetailsService .
     */
    public SpringSecurityConfiguration(
            @Qualifier("userDetailsServiceImpl")
            final UserDetailsService pUserDetailsService) {
        userDetailsService = pUserDetailsService;
    }

    /**
     * Define authentication with userDetailsService.
     * @param auth .
     * @throws Exception .
     */
    @Override
    public void configure(
            final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * Configure Spring Security Filter Chaine.
     * @param http .
     * @throws Exception .
     */
    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login/**", "/logout/**", "/signup/**")
                    .permitAll()
                .antMatchers("/profile/**", "/transfers/**")
                    .hasRole("USER")
                .antMatchers("/admin/**")
                    .hasRole("ADMIN")
                .anyRequest()
                    .hasRole("ADMIN")
                .and()
                    .formLogin()
                    .failureUrl("/login?error=true")
                    .permitAll()
                .and()
                    .logout().logoutSuccessUrl("/logout.html?logSucc=true")
                    .permitAll()
                .and().csrf().disable();
    }

    /**
     * Password Encoder.
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
