package com.secure.notes.config;

import static org.springframework.security.config.Customizer.*;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.secure.notes.model.AppRole;
import com.secure.notes.model.Role;
import com.secure.notes.model.User;
import com.secure.notes.repositories.RoleRepository;
import com.secure.notes.repositories.UserRepository;
//56

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((requests)
			-> requests.anyRequest().authenticated());
		http.csrf(AbstractHttpConfigurer::disable);
		//http.formLogin(withDefaults());
		http.httpBasic(withDefaults());
		return http.build();
	}

	@Bean
	public CommandLineRunner initData(RoleRepository roleRepository,
		UserRepository userRepository) {
		return args -> {
			Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
				.orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

			Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
				.orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

			if (!userRepository.existsByUserName("user1")) {
				User user1 = new User("user1", "user1@example.com", "{noop}password1");
				user1.setAccountNonLocked(false);
				user1.setAccountNonExpired(true);
				user1.setCredentialsNonExpired(true);
				user1.setEnabled(true);
				user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
				user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
				user1.setTwoFactorEnabled(false);
				user1.setSignUpMethod("email");
				user1.setRole(userRole);
				userRepository.save(user1);
			}

			if (!userRepository.existsByUserName("admin")) {
				User admin = new User("admin", "admin@example.com", "{noop}adminPass");
				admin.setAccountNonLocked(true);
				admin.setAccountNonExpired(true);
				admin.setCredentialsNonExpired(true);
				admin.setEnabled(true);
				admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
				admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
				admin.setTwoFactorEnabled(false);
				admin.setSignUpMethod("email");
				admin.setRole(adminRole);
				userRepository.save(admin);
			}
		};
	}
}
