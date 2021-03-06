package book.management.config

import book.management.dao.config.DaoConfig
import book.management.entity.PublisherEntity
import org.seasar.doma.Dao
import org.seasar.doma.Select
import org.seasar.doma.Sql

/**
 * 出版社テーブル操作
 */
@Dao
@DaoConfig
interface PublisherDao {
    /**
     * @param id 出版社ID
     * @return 出版社エンティティ
     */
    @Select
    @Sql("select id, password, name from publishers where id = /* id */'test'")
    fun findPublisher(id: String): PublisherEntity?
}
