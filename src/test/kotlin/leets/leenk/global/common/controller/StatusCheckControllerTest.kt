package leets.leenk.global.common.controller

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import leets.leenk.global.auth.application.property.OauthProperty
import leets.leenk.global.auth.domain.handler.CustomAccessDeniedHandler
import leets.leenk.global.auth.domain.handler.CustomAuthenticationEntryPoint
import leets.leenk.global.config.PermitUrlConfig
import leets.leenk.global.config.SecurityConfig
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StatusCheckController::class)
@Import(SecurityConfig::class, PermitUrlConfig::class)
class StatusCheckControllerTest(
    private val mockMvc: MockMvc,
    @MockkBean private val jwtDecoder: JwtDecoder,
    @MockkBean private val oauthProperty: OauthProperty,
    @MockkBean private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    @MockkBean private val customAccessDeniedHandler: CustomAccessDeniedHandler,
) : StringSpec({
        extensions(SpringExtension)

        "헬스 체크 요청 시 Security 필터가 활성화된 상태에서 인증 없이 접근 가능해야 한다" {
            mockMvc
                .perform(get("/health-check"))
                .andExpect(status().isOk)
                .andExpect(content().string("OK"))
        }
    })
