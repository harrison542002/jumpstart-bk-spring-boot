package com.jumpstart.org.security.oauth;

import com.jumpstart.org.exception.OAuth2AuthenticationProcessingException;
import com.jumpstart.org.models.Role;
import com.jumpstart.org.models.User;
import com.jumpstart.org.models.UserRole;
import com.jumpstart.org.repositories.RoleRepository;
import com.jumpstart.org.repositories.UserRepository;
import com.jumpstart.org.repositories.UserRoleRepository;
import com.jumpstart.org.security.UserPrincipal;
import com.jumpstart.org.services.CartServices;
import com.jumpstart.org.status.AppConstants;
import com.jumpstart.org.status.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Service
public class CustomOAuthUserService extends DefaultOAuth2UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private CartServices cartServices;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest)
			throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		try {
			return processOAuthUser(userRequest, oAuth2User);
		} catch (OAuth2AuthenticationProcessingException e) {
			throw new OAuth2AuthenticationException(new OAuth2Error(HttpStatus.CONFLICT.toString()), e.getMsg());
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception ex) {
			// Throwing an instance of AuthenticationException will trigger the
			// OAuth2AuthenticationFailureHandler
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	@Transactional
	private OAuth2User processOAuthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
		OAuthUserInfo oAuthUserInfo = OAuth2UserInfoFactory
				.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
		if (StringUtils.isEmpty(oAuthUserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		Optional<User> userOptional = userRepository.findByEmail(oAuthUserInfo.getEmail());
		User user;
		if (userOptional.isPresent()) {
			user = userOptional.get();
			if (!user.getProvider()
					.equalsIgnoreCase(Provider.valueOf(userRequest.getClientRegistration().getRegistrationId()).toString())) {
				throw new OAuth2AuthenticationProcessingException(
						"Looks like you're signed up with " + userRequest.getClientRegistration().getRegistrationId()
								+ " account. Please use your " + user.getProvider() + " account to login");
			}
			user = updateExistingUser(user, oAuthUserInfo);
		} else {
			user = registerNewUser(userRequest, oAuthUserInfo);
		}
		return UserPrincipal.create(user, oAuth2User.getAttributes());
	}

	private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuthUserInfo oAuth2UserInfo) {
		User user = new User();
		user.setProvider(Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()).name());
		user.setEmail(oAuth2UserInfo.getEmail());
		user.setFirstName(oAuth2UserInfo.getName());
		user.setLastName(oAuth2UserInfo.getName());
		user = userRepository.save(user);
		this.cartServices.createCart(user);
		UserRole userRole = new UserRole();
		userRole.setUser(user);
		Role memberRole = roleRepository.findById(AppConstants.ROLE_MEMBER.longValue()).get();
		userRole.setRole(memberRole);
		userRoleRepository.save(userRole);
		return user;
	}

	private User updateExistingUser(User existingUser, OAuthUserInfo oAuth2UserInfo) {
		User user = existingUser;
		user.setFirstName(oAuth2UserInfo.getName());
		return userRepository.save(existingUser);
	}
}