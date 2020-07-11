package ses.db.entity

import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.jdbc.entity.NamingType

/**
 * 出版社エンティティ
 */
@Entity(immutable = true, naming = NamingType.SNAKE_UPPER_CASE)
@Table(name = "publishers")
data class PublisherEntity(
    /** ログインid */
    @Id
    val id: String,
    /** パスワード */
    val password: String,
    /** 出版社名 */
    val name: String
)
