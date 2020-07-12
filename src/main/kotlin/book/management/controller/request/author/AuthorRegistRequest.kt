package book.management.controller.request.author

import book.management.entity.AuthorEntity
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/**
 * 著者情報登録リクエストフォーム
 */
@Introspected
class AuthorRegistRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 50)
    val name: String,
    @field:Size(min = 0, max = 255)
    val profile: String?
) {
    fun toEntity(): AuthorEntity {
        return AuthorEntity(name, profile)
    }
}
