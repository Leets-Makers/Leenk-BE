package leets.leenk.global.auth.application.dto.apple

data class AppleTokenResponse(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Long?,
    val refresh_token: String?,
    val id_token: String?,
)
