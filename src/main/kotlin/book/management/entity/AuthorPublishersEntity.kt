package book.management.entity

import org.seasar.doma.Entity
import org.seasar.doma.jdbc.entity.NamingType

/**
 * 書籍著者エンティティ
 */
@Entity(immutable = true, naming = NamingType.SNAKE_UPPER_CASE)
data class AuthorPublishersEntity(
    /** 著者ID */
    val authorId: Long,
    /** 出版社ID */
    val publisherId: String?
)
