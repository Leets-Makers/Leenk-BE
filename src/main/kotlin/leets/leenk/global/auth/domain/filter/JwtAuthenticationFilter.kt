package leets.leenk.global.auth.domain.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import leets.leenk.global.auth.domain.service.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val token = extractTokenFromRequest(request)

            if (token != null && jwtTokenProvider.validateToken(token)) {
                val userId = jwtTokenProvider.getUserIdFromToken(token)

                if (userId != null) {
                    // 인증 객체 생성 (authorities는 빈 리스트)
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            emptyList(),
                        )

                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    // SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().authentication = authentication

                    log.debug("JWT 인증 성공: userId={}", userId)
                }
            }
        } catch (e: Exception) {
            log.error("JWT 인증 처리 중 오류 발생", e)
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Authorization 헤더에서 JWT 토큰 추출
     */
    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }
}
