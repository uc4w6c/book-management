package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.BookDao
import book.management.dao.config.Transactional
import book.management.entity.AuthorEntity
import book.management.entity.BookAuthorPublisherEntity
import book.management.entity.BookAuthorsEntity
import book.management.entity.BookEntity
import book.management.exception.DataNotFoundException
import book.management.exception.NotUpdatableException
import book.management.exception.PublisherPermissionException
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
     * 書籍IDで書籍を検索
     * @param id 書籍ID
     * @return 書籍
     */
    fun findById(id: Long): BookEntity? {
        return bookDao.findById(id)
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

        val book = bookDao.insert(bookEntity).entity

        for (author in authors) {
            bookDao.insertBookAuthorsEntity(BookAuthorsEntity(book.id!!, author.id!!))
        }
        return book
    }

    /**
     * 書籍を更新
     * @param BookEntity 書籍エンティティ
     * @return 更新後の書籍エンティティ
     */
    fun update(publisherId: String, updateBookEntity: BookEntity, authorIdList: List<Long>): BookEntity {
        val beforeBook = bookDao.findById(updateBookEntity.id!!) ?: throw DataNotFoundException("指定した書籍は登録されていません。")

        // 更新対象書籍の出版社IDが異なる場合はエラー
        if (beforeBook!!.publisherId != publisherId)
            throw PublisherPermissionException("指定した書籍は更新できません。")

        // 書籍更新に関するエラーチェック
        val today = LocalDate.now()
        if (beforeBook.publicationDate <= today) {
            if (!(beforeBook.title?: "").equals(updateBookEntity.title?: ""))
                throw NotUpdatableException("出版された書籍のタイトルは変更できません。")
            if (beforeBook.publicationDate != updateBookEntity.publicationDate)
                throw NotUpdatableException("出版された書籍の出版日は変更できません。")
        }
        if (updateBookEntity.publicationDate < today) throw NotUpdatableException("出版日を過去日に変更することはできません。")

        val authors = authorDao.findByIdList(authorIdList)
        if (authors.isEmpty()) throw DataNotFoundException("指定した著者は登録されていません。")

        bookDao.deleteBookAuthorsEntity(updateBookEntity.id!!)
        for (author in authors) {
            bookDao.insertBookAuthorsEntity(BookAuthorsEntity(updateBookEntity.id!!, author.id!!))
        }

        return bookDao.update(updateBookEntity).entity
    }

    /**
     * 書籍を削除
     * @param List<Long> 削除対象書籍IDリスト
     */
    fun delete(publisherId: String, bookIdList: List<Long>) {
        val bookEntityList = bookDao.findByIdList(bookIdList)
        if (bookEntityList.any { bookEntity -> publisherId != bookEntity.publisherId })
            throw PublisherPermissionException("指定した書籍は削除できません。")

        val deleteIdList = bookEntityList.map { it.id!! }
        bookDao.deleteBookAuthorsEntityByBookIdList(deleteIdList)
        bookDao.deleteByBookIdList(deleteIdList)
    }
}
