package dev.swanhtet.authenticationwithinterceptor.configuration;

import dev.swanhtet.authenticationwithinterceptor.repositories.TokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.util.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UUIDAuthorizationFilter extends BasicAuthenticationFilter {

	private final TokenRepository tokenRepository;

	public UUIDAuthorizationFilter(AuthenticationManager authenticationManager, TokenRepository tokenRepository) {
		super(authenticationManager);
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String uuid = request.getHeader("UUID");
		if (uuid != null) {
			// Check if the UUID exists in the database
			if (tokenRepository.existsByToken(uuid)) {
				// Set authentication in SecurityContext
				// You can customize the authentication object based on your requirements
				Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, null);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				// Handle unauthorized access
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		} else {
			// Handle missing UUID header
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		chain.doFilter(request, response);
	}
}
