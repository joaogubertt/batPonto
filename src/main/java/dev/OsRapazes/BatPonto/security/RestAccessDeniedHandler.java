package dev.OsRapazes.BatPonto.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.OsRapazes.BatPonto.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public RestAccessDeniedHandler() {
        mapper = new ObjectMapper();
        // Registra o módulo JavaTimeModule para lidar com Instant, LocalDateTime etc.
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var body = new ApiErrorResponse(
                Instant.now(),
                403,
                "FORBIDDEN",
                "Você não tem permissão para acessar este recurso.",
                req.getRequestURI()
        );

        res.getWriter().write(mapper.writeValueAsString(body));
    }
}
