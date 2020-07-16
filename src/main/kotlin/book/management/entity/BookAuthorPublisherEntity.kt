package book.management.entity

import java.time.LocalDate
import org.seasar.doma.Entity
import org.seasar.doma.Transient
import org.seasar.doma.jdbc.entity.NamingType

/**
 * 書籍詳細情報エンティティ
 */
@Entity(immutable = true, naming = NamingType.SNAKE_UPPER_CASE)
data class BookAuthorPublisherEntity(
    /** id */
    val id: Long,
    /** タイトル */
    val title: String?,
    /** 出版社ID */
    val publisherId: String,
    /** 出版社名 */
    val publisherName: String,
    /** 出版日 */
    val publicationDate: LocalDate,
    /** 概要 */
    val summary: String,
    /** 著者id */
    val authorId: Long?,
    /** 著者名 */
    val authorName: String?,
    /** 著者リスト */
    @Transient
    val authorList: List<AuthorEntity>?
) {
    constructor(
        id: Long,
        title: String?,
        publisherId: String,
        publisherName: String,
        publicationDate: LocalDate,
        summary: String,
        authorId: Long?,
        authorName: String?
    ) : this(id, title, publisherId, publisherName, publicationDate, summary, authorId, authorName, null)
}
