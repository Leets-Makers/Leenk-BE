package leets.leenk.global.common.exception

import org.springframework.http.HttpStatus

interface ErrorCodeInterface {
    val code: Int
    val status: HttpStatus
    val message: String

    // ExplainError 어노테이션에 작성된 설명을 조회하는 메서드
    @Throws(NoSuchFieldException::class)
    fun getExplainError(): String {
        val field = this::class.java.getField((this as Enum<*>).name)
        val annotation = field.getAnnotation(ExplainError::class.java)
        return annotation?.value ?: message
    }
}
