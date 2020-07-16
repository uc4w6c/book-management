package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.BookDao
import book.management.entity.AuthorEntity
import book.management.entity.BookAuthorPublisherEntity
import book.management.entity.BookAuthorsEntity
import book.management.entity.BookEntity
import book.management.exception.DataNotFoundException
import book.management.exception.NotUpdatableException
import book.management.exception.PublisherPermissionException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.seasar.doma.jdbc.Result
import java.time.LocalDate

internal class BookServiceTest {

    @Nested
    class findByTitlePublischer() {
        @Test
        fun `エンティティリストを返却`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()
            val mockDaoReturnList = listOf(
                    BookAuthorPublisherEntity(
                            1,
                            "title",
                            "AA",
                            "AA出版",
                            LocalDate.of(2020, 7, 13),
                            "summary",
                            1,
                            "Taro"
                    ),
                    BookAuthorPublisherEntity(
                            1,
                            "title",
                            "AA",
                            "AA出版",
                            LocalDate.of(2020, 7, 13),
                            "summary",
                            2,
                            "Hanako"
                    )
            )
            val publicationFromDate = LocalDate.of(2020, 7, 1)
            val publicationToDate = LocalDate.of(2020, 7, 1)

            every {
                bookDao.findByTitlePublischer(
                        "title",
                        publicationFromDate,
                        publicationToDate
                )
            } returns mockDaoReturnList

            // exercise
            val authorService = BookService(bookDao, authorDao)
            val actual = authorService.findByTitlePublischer("title", publicationFromDate, publicationToDate)

            // verify
            val expect = listOf(
                    BookAuthorPublisherEntity(
                            1,
                            "title",
                            "AA",
                            "AA出版",
                            LocalDate.of(2020, 7, 13),
                            "summary",
                            null,
                            null,
                            listOf(
                                AuthorEntity(1, "Taro", null),
                                AuthorEntity(2, "Hanako", null)
                            )
                    )
            )

            verify { bookDao.findByTitlePublischer(
                    "title",
                    publicationFromDate,
                    publicationToDate
            ) }
            assertEquals(actual, expect)
        }
    }

    @Nested
    class regist {
        @Test
        fun `authorsが存在しない`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()
            val authorDaoReturnList = listOf<AuthorEntity>()

            val authorIdList = listOf(1L)
            every { authorDao.findByIdList(authorIdList) } returns authorDaoReturnList

            val bookEntity = BookEntity(null, "test", LocalDate.now(), "summary")

            // exercise
            val authorService = BookService(bookDao, authorDao)

            // verify
            assertThrows(DataNotFoundException::class.java) { authorService.regist(bookEntity, authorIdList) }
            verify { authorDao.findByIdList(authorIdList) }
        }

        @Test
        fun `書籍を登録できる`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()
            val authorDaoReturnList = listOf(
                AuthorEntity(1L, "name", "profile")
            )

            val authorIdList = listOf(1L)
            every { authorDao.findByIdList(authorIdList) } returns authorDaoReturnList

            val bookEntity = BookEntity(null, "test", LocalDate.now(), "summary")
            val expect = BookEntity(1L, null, "test", LocalDate.now(), "summary")
            every { bookDao.insert(bookEntity) } returns Result(1, expect)

            val bookAuthorsEntity = BookAuthorsEntity(expect.id!!, authorDaoReturnList[0].id!!)
            every { bookDao.insertBookAuthorsEntity(bookAuthorsEntity) } returns Result(1, bookAuthorsEntity)

            // exercise
            val bookService = BookService(bookDao, authorDao)
            val actual = bookService.regist(bookEntity, authorIdList)

            // verify
            assertEquals(expect, actual)
            verify { authorDao.findByIdList(authorIdList) }
            verify { bookDao.insert(bookEntity) }
            verify { bookDao.insertBookAuthorsEntity(bookAuthorsEntity) }
        }
    }

    @Nested
    class update {
        @Test
        fun `登録されていない書籍を更新エラー`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val updateBookEntity = BookEntity(1L, "title2", "id1", LocalDate.now().plusDays(1), "summary")
            val authorIdList = listOf(1L)

            every { bookDao.findById(updateBookEntity.id!!) } returns null

            // exercise
            val bookService = BookService(bookDao, authorDao)

            // verify
            assertThrows(DataNotFoundException::class.java) { bookService.update(publisherId, updateBookEntity, authorIdList) }
            verify { bookDao.findById(updateBookEntity.id!!) }
        }

        @Test
        fun `更新対象書籍の出版社IDが異なるエラー`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val beforeBook = BookEntity(1L, "title1", "id2", LocalDate.now().plusDays(1), "summary")
            val updateBookEntity = BookEntity(1L, "title2", publisherId, LocalDate.now(), "summary")
            val authorIdList = listOf(1L)

            every { bookDao.findById(updateBookEntity.id!!) } returns beforeBook

            // exercise
            val bookService = BookService(bookDao, authorDao)

            // verify
            assertThrows(PublisherPermissionException::class.java) { bookService.update(publisherId, updateBookEntity, authorIdList) }
            verify { bookDao.findById(updateBookEntity.id!!) }
        }

        @Test
        fun `出版後に書籍タイトル変更エラー`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val beforeBook = BookEntity(1L, "title1", publisherId, LocalDate.now(), "summary")
            val updateBookEntity = BookEntity(1L, "title2", publisherId, LocalDate.now(), "summary")
            val authorIdList = listOf(1L)

            every { bookDao.findById(updateBookEntity.id!!) } returns beforeBook

            // exercise
            val bookService = BookService(bookDao, authorDao)

            // verify
            assertThrows(NotUpdatableException::class.java) { bookService.update(publisherId, updateBookEntity, authorIdList) }
            verify { bookDao.findById(updateBookEntity.id!!) }
        }

        @Test
        fun `出版後に書籍タイトルを変更エラー(Null)`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val beforeBook = BookEntity(1L, "title1", publisherId, LocalDate.now(), "summary")
            val updateBookEntity = BookEntity(1L, null, publisherId, LocalDate.now(), "summary")
            val authorIdList = listOf(1L)

            every { bookDao.findById(updateBookEntity.id!!) } returns beforeBook

            // exercise
            val bookService = BookService(bookDao, authorDao)

            // verify
            assertThrows(NotUpdatableException::class.java) { bookService.update(publisherId, updateBookEntity, authorIdList) }
            verify { bookDao.findById(updateBookEntity.id!!) }
        }

        @Test
        fun `出版後に出版日変更エラー`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val beforeBook = BookEntity(1L, "title1", publisherId, LocalDate.now(), "summary")
            val updateBookEntity = BookEntity(1L, "title1", publisherId, LocalDate.now().plusDays(1), "summary")
            val authorIdList = listOf(1L)

            every { bookDao.findById(updateBookEntity.id!!) } returns beforeBook

            // exercise
            val bookService = BookService(bookDao, authorDao)

            // verify
            assertThrows(NotUpdatableException::class.java) { bookService.update(publisherId, updateBookEntity, authorIdList) }
            verify { bookDao.findById(updateBookEntity.id!!) }
        }

        @Test
        fun `出版日を過去日に変更エラー`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val beforeBook = BookEntity(1L, "title1", publisherId, LocalDate.now().plusDays(1), "summary")
            val updateBookEntity = BookEntity(1L, "title1", publisherId, LocalDate.now().minusDays(1), "summary")
            val authorIdList = listOf(1L)

            every { bookDao.findById(updateBookEntity.id!!) } returns beforeBook

            // exercise
            val bookService = BookService(bookDao, authorDao)

            // verify
            assertThrows(NotUpdatableException::class.java) { bookService.update(publisherId, updateBookEntity, authorIdList) }
            verify { bookDao.findById(updateBookEntity.id!!) }
        }

        @Test
        fun `authorsが存在しないエラー`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val beforeBook = BookEntity(1L, "title1", publisherId, LocalDate.now().plusDays(1), "summary")
            val updateBookEntity = BookEntity(1L, "title2", publisherId, LocalDate.now().plusDays(1), "summary")
            val authorIdList = listOf(1L)

            every { bookDao.findById(updateBookEntity.id!!) } returns beforeBook
            every { authorDao.findByIdList(authorIdList) } returns listOf()

            // exercise
            val bookService = BookService(bookDao, authorDao)

            // verify
            assertThrows(DataNotFoundException::class.java) { bookService.update(publisherId, updateBookEntity, authorIdList) }
            verify { bookDao.findById(updateBookEntity.id!!) }
            verify { authorDao.findByIdList(authorIdList) }
        }

        @Test
        fun `更新可能`() {
            // setup
            val bookDao = mockk<BookDao>()
            val authorDao = mockk<AuthorDao>()

            val publisherId = "id1"
            val beforeBook = BookEntity(1L, "title1", publisherId, LocalDate.now().plusDays(1), "summary1")
            val updateBookEntity = BookEntity(1L, "title2", publisherId, LocalDate.now().plusDays(2), "summary2")
            val authorIdList = listOf(1L)
            val bookAuthorsEntity = BookAuthorsEntity(1L, 1L)
            val authors = listOf(AuthorEntity(1, "Taro", null))

            // List<AuthorEntity>
            every { bookDao.findById(updateBookEntity.id!!) } returns beforeBook
            every { authorDao.findByIdList(authorIdList) } returns authors
            every { bookDao.deleteBookAuthorsEntity(updateBookEntity.id!!) } returns 1
            every { bookDao.insertBookAuthorsEntity(bookAuthorsEntity) } returns Result(1, bookAuthorsEntity)
            every { bookDao.update(updateBookEntity) } returns Result(1, updateBookEntity)

            // exercise
            val bookService = BookService(bookDao, authorDao)
            val actual = bookService.update(publisherId, updateBookEntity, authorIdList)

            // verify
            assertEquals(updateBookEntity, actual)
            verify { bookDao.findById(updateBookEntity.id!!) }
            verify { authorDao.findByIdList(authorIdList) }
            verify { bookDao.deleteBookAuthorsEntity(updateBookEntity.id!!) }
            verify { bookDao.insertBookAuthorsEntity(bookAuthorsEntity) }
            verify { bookDao.update(updateBookEntity) }
        }
    }
}
