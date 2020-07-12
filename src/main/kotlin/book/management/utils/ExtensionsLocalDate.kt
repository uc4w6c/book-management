package book.management.utils

import java.time.LocalDate

/**
 * 指定した日付の月末日を取得
 * @param date 日付
 * @return LocalDate 月末日
 */
fun LocalDate.getLastDayOfMonth(): LocalDate {
    val nextMonthDay = this.plusMonths(1)
    return LocalDate.of(nextMonthDay.year, nextMonthDay.month, 1).minusDays(1)
}

/**
 * 指定した日付の月初日を取得
 * @param date 日付
 * @return LocalDate 月初日
 */
fun LocalDate.getFirstDayOfMonth(): LocalDate {
    return LocalDate.of(this.year, this.month, 1)
}
