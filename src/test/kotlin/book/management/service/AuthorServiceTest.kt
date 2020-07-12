package book.management.service

import book.management.dao.config.AuthorDao
import book.management.entity.AuthorEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.seasar.doma.jdbc.Result

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

    @Nested
    class regist {
        @Test
        fun `insert可能`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val expect = AuthorEntity("Taro", "profile1")
            every { authorDao.insert(expect) } returns Result(0, expect)

            // exercise
            val authorService = AuthorService(authorDao)
            val actual = authorService.regist(expect)

            // verify
            verify { authorDao.insert(expect) }
            assertEquals(actual.name, expect.name)
            assertEquals(actual.profile, expect.profile)
        }
    }
}
