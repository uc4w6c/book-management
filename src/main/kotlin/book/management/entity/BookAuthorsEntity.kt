package book.management.entity

import org.seasar.doma.Entity
import org.seasar.doma.GeneratedValue
import org.seasar.doma.GenerationType
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.jdbc.entity.NamingType
import java.time.LocalDate

/**
 * 書籍著者エンティティ
 */
@Entity(immutable = true, naming = NamingType.SNAKE_UPPER_CASE)
@Table(name = "book_authors")
data class BookAuthorsEntity(
    /** id */
    @Id
    val bookId: Long,
    /** id */
    @Id
    val authorId: Long
)
