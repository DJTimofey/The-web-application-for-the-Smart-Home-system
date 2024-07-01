package com.example.smarthomeapp.config;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.service.UserDetailsServiceImpl;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    private UserService userService;

    @Bean
    @Lazy
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requiresChannel()
                .antMatchers("/login", "/logs", "/logout", "/register", "/registration", "/forgot-password", "/reset-password", "/order-passed", "/qr-redirect", "/img/**").requiresSecure()
                .anyRequest().requiresSecure()
                .and()
                .authorizeRequests()
                .antMatchers("/call-service", "/device", "/devices/add", "/devices/sensorTaH", "/temperatureandhumidityscenarios/add", "devices/myscenarios", "/temperatureandhumidityscenarios/delete/{id}", "/waterleakscenarios/add", "/devices/${device.id}/setBrightness", "light_scenarios/add").hasAnyRole("USER")
                .antMatchers("/admin/admin-service").hasAnyRole("ADMIN")
                .antMatchers("/login", "/logs", "/logout", "/register", "/registration", "/forgot-password", "/reset-password", "/order-passed", "/qr-redirect", "/img/**", "/sensor-data", "/device/temperature-and-humidity", "/sensor_data_history").permitAll() // Добавлено "/sensor_data_history"
                .antMatchers("/dashboard").hasAnyRole("USER")
                .antMatchers("/admin", "/admin/users").hasAnyRole("ADMIN")
                .antMatchers("/admin/check-bills-payment").hasAnyRole("ADMIN")
                .antMatchers("/operator", "/operator-service").hasAnyRole("OPERATOR") // Новое правило для роли OPERATOR
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining());
                    if (role.equals("ROLE_USER")) {
                        response.sendRedirect("/dashboard");
                    } else if (role.equals("ROLE_ADMIN")) {
                        response.sendRedirect("/admin");
                    } else if (role.equals("ROLE_OPERATOR")) { // Добавлено условие для роли OPERATOR
                        response.sendRedirect("/operator");
                    } else {
                        response.sendRedirect("/login?error");
                    }
                })
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedPage("/access-denied")
                .and()
                .portMapper()
                .http(8443).mapsTo(8443)
                .and()
                .exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login?error");
                })
                .and()
                .addFilterBefore(new OncePerRequestFilter() {

                    @Override
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                        if ("/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
                            User user = userService.findUserByUsername(request.getParameter("username"));
                            if (user != null && user.isBlocked()) {
                                response.sendRedirect("/login?blocked=true");
                                return;
                            }
                        }
                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class);
    }
}