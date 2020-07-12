package book.management.dao.config

import book.management.entity.AuthorEntity
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Sql
import org.seasar.doma.Update
import org.seasar.doma.jdbc.Result

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
     * idで著者を検索
     * @param id 著者id
     * @return 著者エンティティ
     */
    @Select
    @Sql("select id, name, profile from authors where id = /* id */1")
    fun findById(id: Long): AuthorEntity?

    /**
     * 名前で著者を検索
     * @param name 著者名
     * @return 著者エンティティ
     */
    @Select
    @Sql("select id, name, profile from authors where name like /* @infix(name) */'田中'")
    fun findByName(name: String): List<AuthorEntity>

    /**
     * 著者を登録
     * @param AuthorEntity 著者エンティティ
     * @return Result<AuthorEntity> 著者エンティティ
     */
    @Insert
    fun insert(authorEntity: AuthorEntity): Result<AuthorEntity>

    /**
     * 著者を更新
     * @param AuthorEntity 著者エンティティ
     * @return Result<AuthorEntity> 著者エンティティ
     */
    @Update
    fun update(authorEntity: AuthorEntity): Result<AuthorEntity>
}
