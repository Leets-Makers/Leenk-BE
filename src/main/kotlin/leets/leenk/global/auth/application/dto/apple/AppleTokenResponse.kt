package leets.leenk.global.auth.application.dto.apple

data class AppleTokenResponse(
    val accessToken: String?,
    val tokenType: String?,
    val expiresIn: Long?,
    val refreshToken: String?,
    val idToken: String?,
)
