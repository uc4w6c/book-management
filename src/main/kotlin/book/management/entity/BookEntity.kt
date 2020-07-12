package book.management.entity

import org.seasar.doma.Entity
import org.seasar.doma.GeneratedValue
import org.seasar.doma.GenerationType
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.jdbc.entity.NamingType
import java.time.LocalDate

/**
 * 書籍エンティティ
 */
@Entity(immutable = true, naming = NamingType.SNAKE_UPPER_CASE)
@Table(name = "books")
class BookEntity(
    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    /** タイトル */
    val title: String?,
    /** 出版社ID */
    val publisherId: String,
    /** 出版日 */
    val publicationDate: LocalDate,
    /** 概要 */
    val summary: String
) {
    constructor(
        title: String?,
        publisherId: String,
        publicationDate: LocalDate,
        summary: String
    ) : this(null, title, publisherId, publicationDate, summary)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "BookEntity(id=$id, title=$title, publisherId='$publisherId', publicationDate=$publicationDate, summary='$summary')"
    }
}
