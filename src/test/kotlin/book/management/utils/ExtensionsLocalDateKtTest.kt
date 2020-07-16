package book.management.utils

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ExtensionsLocalDateKtTest {

    @Nested
    class getLastDayOfMonth() {
        @Test
        fun `月末日が取得できる`() {
            // setup
            val date = LocalDate.of(2020, 12, 15)
            val expect = LocalDate.of(2020, 12, 31)

            // exercise
            val actual = date.getLastDayOfMonth()

            // verify
            assertEquals(expect, actual)
        }
    }

    @Nested
    class getFirstDayOfMonth() {
        @Test
        fun `月初1日が取得できる`() {
            // setup
            val date = LocalDate.of(2020, 12, 15)
            val expect = LocalDate.of(2020, 12, 1)

            // exercise
            val actual = date.getFirstDayOfMonth()

            // verify
            assertEquals(expect, actual)
        }
    }
}
