package leets.leenk.global.common.exception

import org.springframework.http.HttpStatus
import java.util.*

interface ErrorCodeInterface {
    val code: Int
    val status: HttpStatus
    val message: String

    // ExplainError 어노테이션에 작성된 설명을 조회하는 메서드
    @Throws(NoSuchFieldException::class)
    fun getExplainError(): String {
        val field = this.javaClass.getField((this as Enum<*>).name)
        val annotation = field.getAnnotation<ExplainError?>(ExplainError::class.java)
        return if (Objects.nonNull(annotation)) annotation!!.value else message
    }
}
