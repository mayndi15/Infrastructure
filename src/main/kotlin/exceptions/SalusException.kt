package exceptions

import org.springframework.http.HttpStatus

class SalusException(
    private val messages: Message,
    language: String,
    vararg vars: Any
) : Exception(messages.getReadableText(*vars, language = language)) {
    val code: Long
        get() = messages.code

    val httpStatus: HttpStatus
        get() = messages.httpStatus
}