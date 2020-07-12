package book.management.service

import book.management.dao.config.AuthorDao
import book.management.entity.AuthorEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.seasar.doma.jdbc.Result

internal class AuthorServiceTest {

    @Nested
    class findById {
        @Test
        fun `Entityを取得`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val expect = AuthorEntity(1, "Taro", "profile1")
            every { authorDao.findById(1) } returns expect

            // exercise
            val authorService = AuthorService(authorDao)
            val actual = authorService.findById(1)

            // verify
            verify { authorDao.findById(1) }
            assertEquals(actual, expect)
        }

        @Test
        fun `Nullを返却`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            every { authorDao.findById(1) } returns null

            // exercise
            val authorService = AuthorService(authorDao)
            val actual = authorService.findById(1)

            // verify
            verify { authorDao.findById(1) }
            assertNull(actual)
        }
    }

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
            every { authorDao.insert(expect) } returns Result(1, expect)

            // exercise
            val authorService = AuthorService(authorDao)
            val actual = authorService.regist(expect)

            // verify
            verify { authorDao.insert(expect) }
            assertEquals(actual.name, expect.name)
            assertEquals(actual.profile, expect.profile)
        }
    }

    @Nested
    class update {
        @Test
        fun `update可能`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val expect = AuthorEntity(1, "Taro", "profile1")
            every { authorDao.update(expect) } returns Result(1, expect)

            // exercise
            val authorService = AuthorService(authorDao)
            val actual = authorService.update(expect)

            // verify
            verify { authorDao.update(expect) }
            assertEquals(actual, expect)
        }
    }
}
