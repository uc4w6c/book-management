package book.management.controller.request.book

import book.management.entity.AuthorEntity
import book.management.entity.BookEntity
import book.management.utils.getFirstDayOfMonth
import book.management.utils.getLastDayOfMonth
import io.micronaut.core.annotation.Creator
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.QueryValue
import java.time.LocalDate
import javax.annotation.Nullable
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.constraints.*

/**
 * 書籍情報登録リクエストフォーム
 */
@Introspected
class BookUpdateRequest @Creator constructor(
    /** id */
    @field:NotNull
    val id: Long,
    /** タイトル */
    @field:Size(min = 0, max = 50)
    val title: String?,
    /** 出版日 */
    // TODO: LocalDateに対してのValidationが効いていない
    @field:NotNull
    val publication_date: LocalDate,
    /** 概要 */
    @field:NotBlank
    @field:Size(min = 1, max = 255)
    val summary: String,
    /** 著者IDリスト */
    // TODO: Listに対してのValidationが効いていない
    @field:NotNull
    val author_id_list: List<Long>
) {
    fun toEntity(publisherId: String): BookEntity {
        return BookEntity(this.id, this.title, publisherId, this.publication_date, this.summary)
    }
}

