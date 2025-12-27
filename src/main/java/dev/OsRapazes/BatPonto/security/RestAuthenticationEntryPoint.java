package dev.OsRapazes.BatPonto.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.OsRapazes.BatPonto.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public RestAuthenticationEntryPoint() {
        mapper = new ObjectMapper();
        // Registra o módulo JavaTimeModule para lidar com Instant, LocalDateTime etc.
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
            throws IOException {

        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");

        var body = new ApiErrorResponse(
                Instant.now(),
                401,
                "UNAUTHORIZED",
                "Token ausente ou inválido.",
                req.getRequestURI()
        );

        res.getWriter().write(mapper.writeValueAsString(body));
    }
}
