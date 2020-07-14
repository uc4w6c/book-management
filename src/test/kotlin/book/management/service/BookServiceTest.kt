package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.BookDao
import book.management.entity.AuthorEntity
import book.management.entity.BookAuthorPublisherEntity
import book.management.entity.BookAuthorsEntity
import book.management.entity.BookEntity
import book.management.exception.DataNotFoundException
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
}
