package book.management.dao.config

import book.management.entity.BookAuthorPublisherEntity
import book.management.entity.BookAuthorsEntity
import book.management.entity.BookEntity
import org.seasar.doma.Dao
import org.seasar.doma.Insert
import org.seasar.doma.Select
import org.seasar.doma.Sql
import org.seasar.doma.jdbc.Result
import java.time.LocalDate

/**
 * 書籍テーブル操作
 */
@Dao
@DaoConfig
interface BookDao {
    /**
     * 著者データをタイトルと出版日で検索
     * @param title タイトル
     * @param publicationFromDate 出版日検索from
     * @param publicationToDate 出版日検索to
     * @return 著者エンティティ
     */
    @Select
    @Sql("""
       select
           books.id as id,
           books.title as title,
           books.publisher_id as publisher_id,
           publishers.name as publisher_name,
           books.publication_date as publication_date,
           books.summary as summary, 
           book_authors.author_id as author_id,
           authors.name as author_name
       from
           books
       inner join
           publishers
           on books.publisher_id = publishers.id
       inner join
           book_authors
           on books.id = book_authors.book_id
       inner join
           authors
           on book_authors.author_id = authors.id
       where
           books.publication_date >= /* publicationFromDate */'1990-01-01'
           and books.publication_date <= /* publicationToDate */'2020-07-30'
           /*%if title != null */
               and books.title like /* @infix(title) */'リーダブル'
           /*%end*/
    """)
    fun findByTitlePublischer(title: String?, publicationFromDate: LocalDate, publicationToDate: LocalDate): List<BookAuthorPublisherEntity>

    /**
     * 書籍を登録
     * @param BookEntity 書籍エンティティ
     * @return Result<BookEntity> 書籍エンティティ
     */
    @Insert
    fun insert(bookEntity: BookEntity): Result<BookEntity>

    /**
     * 書籍著者エンティティを登録
     * @param BookAuthorsEntity 書籍著者エンティティ
     * @return Result<BookAuthorsEntity> 書籍著者エンティティ
     */
    @Insert
    fun insertBookAuthorsEntity(bookAuthorsEntity: BookAuthorsEntity): Result<BookAuthorsEntity>

}
