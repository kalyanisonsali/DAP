package com.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Disable CSRF for testing
				.authorizeRequests(auth -> auth.antMatchers("/", "/signup", "/savedata", "/check-email").permitAll()
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2.successHandler(successHandler()))

				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Logout URL
						.logoutSuccessUrl("/") // Redirect after logout
						.invalidateHttpSession(true) // Invalidate session
						.deleteCookies("JSESSIONID") // Delete session cookies
						.permitAll()); // OAuth2 Login Handling

		return http.build();
	}

	@Bean
	public AuthenticationSuccessHandler successHandler() {
		return new SimpleUrlAuthenticationSuccessHandler("/dash");
	}
}
