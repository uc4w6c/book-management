package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.BookDao
import book.management.dao.config.Transactional
import book.management.entity.AuthorEntity
import book.management.entity.BookAuthorPublisherEntity
import book.management.entity.BookEntity
import book.management.entity.BookAuthorsEntity
import book.management.exception.DataNotFoundException
import java.time.LocalDate
import javax.inject.Singleton

/**
 * 書籍サービス
 */
@Singleton
@Transactional
class BookService(private val bookDao: BookDao, private val authorDao: AuthorDao) {
    /**
     * 書籍タイトル・出版日で検索
     * @param title タイトル
     * @return 著者リスト
     */
    fun findByTitlePublischer(title: String?, publicationFromDate: LocalDate, publicationToDate: LocalDate): List<BookAuthorPublisherEntity> {
        val bookList = bookDao.findByTitlePublischer(title, publicationFromDate, publicationToDate)

        // APサーバで扱い易い形に変換(authorListを別で保持させる)
        return bookList.associate {
            val authorList = bookList.filter { book -> book.id == it.id }.map {
                AuthorEntity(it.authorId, it.authorName!!, null)
            }.toList()
            it.id to BookAuthorPublisherEntity(
                    it.id, it.title, it.publisherId, it.publisherName,
                    it.publicationDate, it.summary, null, null,
                    authorList)
        }.values.toList()
    }

    /**
     * 書籍を登録
     * @param BookEntity 書籍エンティティ
     * @param authorId 著者ID
     * @return BookEntity 書籍エンティティ
     */
    fun regist(bookEntity: BookEntity, authorIdList: List<Long>): BookEntity {
        val authors = authorDao.findByIdList(authorIdList)
        if (authors.size == 0) throw DataNotFoundException("指定した著者は登録されていません。")

        val book = bookDao.insert(bookEntity).getEntity()

        for (author in authors) {
            bookDao.insertBookAuthorsEntity(BookAuthorsEntity(book.id!!, author.id!!))
        }
        return book
    }

}
