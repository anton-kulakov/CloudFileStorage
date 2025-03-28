package dev.anton_kulakov.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.anton_kulakov.dto.UserRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JsonAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) {
        try {
            UserRequestDto userRequestDto = objectMapper.readValue(req.getInputStream(), UserRequestDto.class);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userRequestDto.getUsername(), userRequestDto.getPassword());

            return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse login request", e);
        }
    }
}
