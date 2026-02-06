package leets.leenk.global.auth.application.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth.jwt")
data class JwtProperty(
    val secret: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
)
