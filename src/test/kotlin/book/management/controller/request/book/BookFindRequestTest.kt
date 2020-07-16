package book.management.controller.request.book

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BookFindRequestTest {

    @Nested
    class isValid() {
        @Test
        fun `正常値チェック`() {
            // setup
            val bookFindRequest = BookFindRequest(
                                "title",
                                2020,
                                7,
                                2020,
                                7
            )

            // exercise
            val actual = bookFindRequest.isValid()

            // verify
            assertTrue(actual)
        }

        @Test
        fun `検索終了日よりも検索開始日の方が未来日付`() {
            // setup
            val bookFindRequest = BookFindRequest(
                    "title",
                    2020,
                    8,
                    2020,
                    7
            )

            // exercise
            val actual = bookFindRequest.isValid()

            // verify
            assertFalse(actual)
        }

        @Test
        fun `検索開始日が検索可能From日よりも過去日付`() {
            // setup
            val errorFromDate = BookFindRequest.searchablePublicationFromDate().minusMonths(1)
            val bookFindRequest = BookFindRequest(
                    "title",
                    errorFromDate.year,
                    errorFromDate.month.value,
                    errorFromDate.year,
                    errorFromDate.month.value
            )

            // exercise
            val actual = bookFindRequest.isValid()

            // verify
            assertFalse(actual)
        }

        @Test
        fun `検索終了日が検索可能To日よりも未来日付`() {
            // setup
            val errorToDate = BookFindRequest.searchablePublicationToDate().plusMonths(1)
            val bookFindRequest = BookFindRequest(
                    "title",
                    errorToDate.year,
                    errorToDate.month.value,
                    errorToDate.year,
                    errorToDate.month.value
            )

            // exercise
            val actual = bookFindRequest.isValid()

            // verify
            assertFalse(actual)
        }
    }
}
