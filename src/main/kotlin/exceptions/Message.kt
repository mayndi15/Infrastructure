package exceptions

import exceptions.I18N.getString
import org.springframework.http.HttpStatus
enum class Message(
    val httpStatus: HttpStatus,
    val code: Long,
    val key: String
) {
    ID_NOT_FOUND(HttpStatus.BAD_REQUEST, 1L, "id.not.found");

    fun getReadableText(vararg args: Any, language: String): String {
        return getString(this.key, language, *args)
    }
}