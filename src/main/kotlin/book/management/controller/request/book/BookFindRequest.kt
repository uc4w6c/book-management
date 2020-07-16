package book.management.controller.request.book

import book.management.utils.getFirstDayOfMonth
import book.management.utils.getLastDayOfMonth
import io.micronaut.core.annotation.Creator
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.QueryValue
import java.time.LocalDate
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

/**
 * 書籍検索リクエストフォーム
 */
@Introspected
class BookFindRequest @Creator constructor(
    /** タイトル */
    @field:QueryValue
    @field:Size(min = 0, max = 50)
    val title: String?,
    /** 出版日 年from */
    @field:QueryValue
    val publication_year_from: Int,
    /** 出版日 月from */
    @field:Min(1)
    @field:Max(12)
    @field:QueryValue
    val publication_month_from: Int,
    /** 出版日 年to */
    @field:QueryValue
    val publication_year_to: Int,
    /** 出版日 月to */
    @field:Min(1)
    @field:Max(12)
    @field:QueryValue
    val publication_month_to: Int
) {
    private val errorMessages = mutableListOf<String>()

    companion object {
        /**
         * デフォルト検索条件を生成
         */
        fun defaultBuild(): BookFindRequest {
            val defaultPublicationFromDate = defaultPublicationFromDate()
            val defaultPublicationToDate = defaultPublicationToDate()

            return BookFindRequest(null,
                                    defaultPublicationFromDate.year,
                                    defaultPublicationFromDate.monthValue,
                                    defaultPublicationToDate.year,
                                    defaultPublicationToDate.monthValue)
        }

        /**
         * デフォルトの検索出版From日を生成
         * @return LocalDate デフォルトの検索出版From日
         */
        fun defaultPublicationFromDate(): LocalDate {
            val nowDate = LocalDate.now()
            return nowDate.minusMonths(1).getFirstDayOfMonth()
        }

        /**
         * デフォルトの検索出版To日を生成
         * @return LocalDate デフォルトの検索出版To日
         */
        fun defaultPublicationToDate(): LocalDate {
            val nowDate = LocalDate.now()
            return nowDate.plusMonths(1).getLastDayOfMonth()
        }

        /**
         * 検索可能出版From日を生成
         * @return LocalDate 検索可能出版From日
         */
        fun searchablePublicationFromDate(): LocalDate {
            return LocalDate.of(1980, 1, 1)
        }

        /**
         * 検索可能出版To日を生成
         * @return LocalDate 検索可能出版To日
         */
        fun searchablePublicationToDate(): LocalDate {
            val nowDate = LocalDate.now()
            return nowDate.plusMonths(1).getLastDayOfMonth()
        }
    }

    /**
     * Validationチェック
     * @return bool valid
     */
    fun isValid(): Boolean {
        // MEMO: custom Validationのやり方がわからないため独自の書き方で実装
        // おそらくこれを参考にするべき:https://docs.micronaut.io/latest/guide/index.html#beanValidation
        var isError = false
        val fromDate = LocalDate.of(publication_year_from, publication_month_from, 1)
        val toDate = LocalDate.of(publication_year_to, publication_month_to, 1).getLastDayOfMonth()

        if (fromDate > toDate) {
            errorMessages.add("検索開始日は検索終了日よりも前の年月に設定してください。")
            isError = true
        }

        val searchableFromDate = searchablePublicationFromDate()
        if (fromDate < searchableFromDate) {
            errorMessages.add("検索開始日は" +
                                searchableFromDate.year + "年" +
                                searchableFromDate.month.value + "月" +
                                "以降に設定してください。")
            isError = true
        }
        if (toDate > searchablePublicationToDate()) {
            errorMessages.add("検索終了日は翌月の月末日以前に設定してください。")
            isError = true
        }
        return !isError
    }

    /**
     * @return List<String> エラーメッセージ
     */
    fun getErrorMessages(): List<String> = errorMessages

    /**
     * 検索開始日(YYYYMMDD)を取得
     * @return LocalDate 検索開始日
     */
    fun getPublicationFromDate(): LocalDate {
        return LocalDate.of(this.publication_year_from, this.publication_month_from, 1)
    }

    /**
     * 検索終了日(YYYYMMDD)を取得
     * @return LocalDate 検索終了日
     */
    fun getPublicationToDate(): LocalDate {
        return LocalDate.of(this.publication_year_to, this.publication_month_to, 1).getLastDayOfMonth()
    }
}
