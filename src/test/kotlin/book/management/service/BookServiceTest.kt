package book.management.service

import book.management.dao.config.BookDao
import book.management.entity.AuthorEntity
import book.management.entity.BookAuthorPublisherEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import java.time.LocalDate

internal class BookServiceTest {

    @Nested
    class findByTitlePublischer() {
        @Test
        fun `エンティティリストを返却`() {
            // setup
            val authorDao = mockk<BookDao>()
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
                authorDao.findByTitlePublischer(
                        "title",
                        publicationFromDate,
                        publicationToDate
                )
            } returns mockDaoReturnList

            // exercise
            val authorService = BookService(authorDao)
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

            verify { authorDao.findByTitlePublischer(
                    "title",
                    publicationFromDate,
                    publicationToDate
            ) }
            assertEquals(actual, expect)
        }
    }
}
