package leets.leenk.global.auth.domain.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import leets.leenk.global.auth.application.exception.AuthErrorCode;
import leets.leenk.global.auth.application.util.HandlerResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerResponseUtil handlerResponseUtil;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        handlerResponseUtil.setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage(), AuthErrorCode.OAUTH_ERROR);
    }
}
