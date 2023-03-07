package com.jumpstart.org.security.oauth;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuthUserInfo{

	private Map<String, Object> attributes;
	
	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
		this.attributes = attributes;
	}

	@Override
	public String getId() {
		return (String) attributes.get("sub");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getImageUrl() {
		return (String) attributes.get("picture");
	}

}