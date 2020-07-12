package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.BookDao
import book.management.dao.config.Transactional
import book.management.entity.AuthorEntity
import book.management.entity.BookAuthorPublisherEntity
import book.management.entity.BookEntity
import java.time.LocalDate
import javax.inject.Singleton

/**
 * 書籍サービス
 */
@Singleton
@Transactional
class BookService(private val bookDao: BookDao) {
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

}
