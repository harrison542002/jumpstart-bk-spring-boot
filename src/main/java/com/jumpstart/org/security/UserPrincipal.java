package com.jumpstart.org.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.jumpstart.org.models.Brand;
import com.jumpstart.org.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;


public class UserPrincipal implements UserDetails, OAuth2User{

	private String email;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;	
	
	public UserPrincipal(String email, String password,Collection<? extends GrantedAuthority> authorities) {
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}
	public UserPrincipal(String email, String password, List<String> authorities) {
		this.email = email;
		this.password = password;
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		authorities.forEach((auth) -> grantedAuthorities.add(new SimpleGrantedAuthority(auth)));
		this.authorities = grantedAuthorities;
	}

	public static UserPrincipal createUser(User user) {
		
		if(user.getUserRoles() == null) {
			return new UserPrincipal(user.getEmail(), user.getPassword(),
					new ArrayList<GrantedAuthority>());
		}

		return new UserPrincipal(user.getEmail(), user.getPassword(),
				user.getUserRoles().stream().map((userRole) -> userRole.getRole().getRoleName()).collect(Collectors.toList()));
	}

	public static UserPrincipal create(User user, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = UserPrincipal.createUser(user);
		userPrincipal.setAttributes(attributes);
		return userPrincipal;
	}

	public static UserDetails createBrand(Brand brand) {
		return new UserPrincipal(brand.getBrandEmail(), brand.getPassword(), new ArrayList<GrantedAuthority>());
	}

	private void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		
	}
	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return this.email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
