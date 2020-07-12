package book.management.service

import book.management.dao.config.AuthorDao
import book.management.entity.AuthorEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class AuthorServiceTest {
    @Nested
    class findTest {

        val AUTHOR_LIST = arrayListOf<AuthorEntity>(
            AuthorEntity(1, "Taro", "profile1"),
            AuthorEntity(2, "Hanako", "profile2")
        )

        @Test
        fun `nameがNull`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            every { authorDao.findAll() } returns AUTHOR_LIST

            // exercise
            val authorService = AuthorService(authorDao)
            val actual = authorService.find(null)

            // verify
            verify { authorDao.findAll() }
            assertEquals(actual, AUTHOR_LIST)
        }

        @Test
        fun `nameがNot Null`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val expect = AUTHOR_LIST.filter { Regex("Taro").containsMatchIn(it.name) }
            every { authorDao.findByName("Taro") } returns expect

            // exercise
            val authorService = AuthorService(authorDao)
            val actual = authorService.find("Taro")

            // verify
            verify { authorDao.findByName("Taro") }
            assertEquals(actual, expect)
        }
    }
}
