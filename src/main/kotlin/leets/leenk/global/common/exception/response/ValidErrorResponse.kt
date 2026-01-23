package leets.leenk.global.common.exception.response

data class ValidErrorResponse(
    val errorField: String,
    val errorMessage: String,
    val inputValue: Any?,
) {
    companion object {
        @JvmStatic
        fun of(
            field: String,
            msg: String,
            value: Any?,
        ): ValidErrorResponse = ValidErrorResponse(field, msg, value)
    }
}
