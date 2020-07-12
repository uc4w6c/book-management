package book.management.dao.config

import book.management.entity.AuthorEntity
import org.seasar.doma.Dao
import org.seasar.doma.Select
import org.seasar.doma.Sql

/**
 * 著者テーブル操作
 */
@Dao
@DaoConfig
interface AuthorDao {
    /**
     * 著者データ全てを取得
     * @return 著者エンティティ
     */
    @Select
    @Sql("select id, name, profile from authors")
    fun findAll(): List<AuthorEntity>

    /**
     * @param name 著者名
     * @return 著者エンティティ
     */
    @Select
    @Sql("select id, name, profile from authors where name like /* @infix(name) */'田中'")
    fun findByName(name: String): List<AuthorEntity>
}
