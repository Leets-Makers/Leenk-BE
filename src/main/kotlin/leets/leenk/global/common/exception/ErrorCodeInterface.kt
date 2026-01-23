package leets.leenk.global.common.exception

import org.springframework.http.HttpStatus

interface ErrorCodeInterface {
    val code: Int
    val status: HttpStatus
    val message: String
}
