package leets.leenk.global.auth.application.annotation;

import leets.leenk.global.auth.application.exception.OauthException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static leets.leenk.global.auth.application.usecase.AuthUsecase.JWT_USER_ID_CLAIM;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && (parameter.getParameterType().equals(Long.class)
                || parameter.getParameterType().equals(Long.TYPE));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new OauthException();
        }

        // JWT 필터에서 설정한 principal(userId)을 반환
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return principal;
        }

        // 호환성: 기존 Jwt 객체도 지원
        if (principal instanceof Jwt jwt) {
            return Long.valueOf(jwt.getClaimAsString(JWT_USER_ID_CLAIM));
        }

        throw new OauthException();
    }
}
