package com.jumpstart.org.security.oauth;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jumpstart.org.exception.BadRequestException;
import com.jumpstart.org.status.AppProperties;
import com.jumpstart.org.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private JwtUtils tokenProvider;

	private AppProperties appProperties;

	private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

	@Autowired
	public OAuth2SuccessHandler(JwtUtils tokenProvider, AppProperties appProperties,
			OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
		this.tokenProvider = tokenProvider;
		this.appProperties = appProperties;
		this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String redirectURL = request.getParameter("redirect_uri");
		String targetUrl = determineTargetUrl(redirectURL, response, authentication);
		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}
		clearAuthenticationAttributes(request);
		getRedirectStrategy().sendRedirect(request, response, 
				targetUrl);
	}

	protected String determineTargetUrl(String redirectURL, HttpServletResponse response,
			Authentication authentication) {
		if (StringUtils.isEmpty(redirectURL)) {
			throw new BadRequestException(
					"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}
		String token = tokenProvider.createToken(authentication);
		
		return UriComponentsBuilder.fromUriString(redirectURL).queryParam("token", token).build().toUriString();
	}
}
