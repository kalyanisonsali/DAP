package com.example.demo.Security;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
		Map<String, Object> attributes = oAuth2User.getAttributes();

		System.out.println("User Info: " + attributes);
		return oAuth2User;
	}
}
