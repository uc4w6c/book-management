package book.management.entity

import org.seasar.doma.Entity
import org.seasar.doma.GeneratedValue
import org.seasar.doma.GenerationType
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.jdbc.entity.NamingType

/**
 * 出版社エンティティ
 */
@Entity(immutable = true, naming = NamingType.SNAKE_UPPER_CASE)
@Table(name = "authors")
class AuthorEntity(
    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    /** 著者名 */
    val name: String,
    /** プロフィール */
    val profile: String?
) {
    constructor(name: String, profile: String?) : this(null, name, profile)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthorEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "AuthorEntity(id=$id, name='$name', profile=$profile)"
    }
}
