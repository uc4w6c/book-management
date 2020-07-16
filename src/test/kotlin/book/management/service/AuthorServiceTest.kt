package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.BookDao
import book.management.entity.AuthorEntity
import book.management.entity.AuthorPublishersEntity
import book.management.entity.BookAuthorsEntity
import book.management.exception.PublisherPermissionException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
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
            val bookDao = mockk<BookDao>()
            val expect = AuthorEntity(1, "Taro", "profile1")
            every { authorDao.findById(1) } returns expect

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
            val actual = authorService.findById(1)

            // verify
            verify { authorDao.findById(1) }
            assertEquals(actual, expect)
        }

        @Test
        fun `Nullを返却`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val bookDao = mockk<BookDao>()
            every { authorDao.findById(1) } returns null

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
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
            val bookDao = mockk<BookDao>()
            every { authorDao.findAll() } returns AUTHOR_LIST

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
            val actual = authorService.find(null)

            // verify
            verify { authorDao.findAll() }
            assertEquals(actual, AUTHOR_LIST)
        }

        @Test
        fun `nameがNot Null`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val bookDao = mockk<BookDao>()
            val expect = AUTHOR_LIST.filter { Regex("Taro").containsMatchIn(it.name) }
            every { authorDao.findByName("Taro") } returns expect

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
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
            val bookDao = mockk<BookDao>()
            val expect = AuthorEntity("Taro", "profile1")
            every { authorDao.insert(expect) } returns Result(1, expect)

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
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
            val bookDao = mockk<BookDao>()
            val expect = AuthorEntity(1, "Taro", "profile1")
            every { authorDao.update(expect) } returns Result(1, expect)

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
            val actual = authorService.update(expect)

            // verify
            verify { authorDao.update(expect) }
            assertEquals(actual, expect)
        }
    }

    @Nested
    class uniquePublisherAuthorFindByIdList {
        @Test
        fun `指定した出版社のみで出している著者を返却`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val bookDao = mockk<BookDao>()

            val publisherId = "id1"
            val authorIdList = listOf(1L, 2L)
            val authorPublishersEntityList = listOf(
                    AuthorPublishersEntity(1L, "id1"),
                    AuthorPublishersEntity(2L, "id1"),
                    AuthorPublishersEntity(2L, "id2")
            )
            every { authorDao.findAuthorPublishersByIdList(authorIdList) } returns authorPublishersEntityList
            val expect = setOf(authorPublishersEntityList[0].authorId)

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
            val actual = authorService.uniquePublisherAuthorFindByIdList(publisherId, authorIdList)

            // verify
            assertEquals(expect, actual)
            verify { authorDao.findAuthorPublishersByIdList(authorIdList) }
        }

        @Test
        fun `どこにも出版していない著者を返却`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val bookDao = mockk<BookDao>()

            val publisherId = "id1"
            val authorIdList = listOf(1L, 2L)
            val authorPublishersEntityList = listOf(
                    AuthorPublishersEntity(1L, "id1"),
                    AuthorPublishersEntity(2L, null)
            )
            every { authorDao.findAuthorPublishersByIdList(authorIdList) } returns authorPublishersEntityList
            val expect = authorPublishersEntityList.map { it.authorId }.toSet()

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
            val actual = authorService.uniquePublisherAuthorFindByIdList(publisherId, authorIdList)

            // verify
            assertEquals(expect, actual)
            verify { authorDao.findAuthorPublishersByIdList(authorIdList) }
        }
    }

    @Nested
    class delete {
        @Test
        fun `削除できない著者が存在エラー`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val bookDao = mockk<BookDao>()

            val publisherId = "id1"
            val authorIdList = listOf(1L, 2L)
            val authorPublishersEntityList = listOf(
                    AuthorPublishersEntity(1L, "id1"),
                    AuthorPublishersEntity(2L, "id2")
            )
            every { authorDao.findAuthorPublishersByIdList(authorIdList) } returns authorPublishersEntityList

            // exercise
            val authorService = AuthorService(authorDao, bookDao)

            // verify
            assertThrows(PublisherPermissionException::class.java) { authorService.delete(publisherId, authorIdList) }
            verify { authorDao.findAuthorPublishersByIdList(authorIdList) }
        }

        @Test
        fun `削除可能`() {
            // setup
            val authorDao = mockk<AuthorDao>()
            val bookDao = mockk<BookDao>()

            val publisherId = "id1"
            val authorIdList = listOf(1L, 2L)
            val authorPublishersEntityList = listOf(
                    AuthorPublishersEntity(1L, "id1"),
                    AuthorPublishersEntity(2L, null)
            )
            every { authorDao.findAuthorPublishersByIdList(authorIdList) } returns authorPublishersEntityList

            val bookAuthorsEntityList = listOf(
                BookAuthorsEntity(1L, 1L),
                BookAuthorsEntity(2L, 2L)
            )
            every { bookDao.findBookAuthorsEntityByAuthorId(authorIdList) } returns bookAuthorsEntityList

            val bookIdList = bookAuthorsEntityList.map { it.bookId }
            every { bookDao.deleteBookAuthorsEntityByBookIdList(bookIdList) } returns 2
            every { bookDao.deleteByBookIdList(bookIdList) } returns 2
            every { authorDao.deleteByAuthorIdList(authorIdList) } returns 2

            // exercise
            val authorService = AuthorService(authorDao, bookDao)
            authorService.delete(publisherId, authorIdList)

            // verify
            verify { authorDao.findAuthorPublishersByIdList(authorIdList) }
            verify { bookDao.deleteBookAuthorsEntityByBookIdList(bookIdList) }
            verify { bookDao.deleteByBookIdList(bookIdList) }
            verify { authorDao.deleteByAuthorIdList(authorIdList) }
        }
    }
}
