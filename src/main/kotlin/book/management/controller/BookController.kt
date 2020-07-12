package book.management.controller

import book.management.controller.request.author.AuthorRegistRequest
import book.management.controller.request.author.AuthorUpdateRequest
import book.management.controller.request.book.BookFindRequest
import book.management.service.AuthorService
import book.management.service.BookService
import book.management.utils.getFirstDayOfMonth
import book.management.utils.getLastDayOfMonth
import io.micronaut.context.MessageSource
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.RequestBean
import io.micronaut.http.annotation.Body as Body
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.session.Session
import io.micronaut.validation.Validated
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import java.time.LocalDate
import java.util.HashMap
import javax.validation.ConstraintViolationException
import javax.validation.Valid

/**
 * 書籍操作コントローラ
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/book")
open class BookController(
    private val messageSource: MessageSource,
    private val bookService: BookService
) {

    /**
     * 書籍一覧初期表示
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/")
    @View("book/index")
    fun index(session: Session): Map<String, Any> {
        val data = HashMap<String, Any>()

        val defaultBookFind = BookFindRequest.defaultBuild()

        data.putAll(bookFind(defaultBookFind.title, defaultBookFind.getPublicationFromDate(), defaultBookFind.getPublicationToDate()))
        data.putAll(requestSet(defaultBookFind))

        data["info"] = session.get("info").orElse("false")
        if (session.contains("info")) session.put("info", "")

        return data
    }

    /**
     * 書籍検索
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/find{?bookFindRequest*}")
    // @Get("/find{?title,publication_year_from,publication_month_from,publication_year_to,publication_month_to}")
    @View("book/index")
    open fun find(@Valid /*@RequestBean*/ bookFindRequest: BookFindRequest): Map<String, Any> {
        val data = HashMap<String, Any>()

        data.putAll(requestSet(bookFindRequest))

        if (!bookFindRequest.isValid()) {
            data.put("errors", bookFindRequest.getErrorMessages())
            return data
        }

        data.putAll(bookFind(bookFindRequest.title, bookFindRequest.getPublicationFromDate(), bookFindRequest.getPublicationToDate()))
        return data
    }

    /**
     * リクエストデータを返却する
     */
    private fun requestSet(bookFindRequest: BookFindRequest): Map<String, Any> {
        val data = HashMap<String, Any>()
        data["limitPublicationFromYear"] = BookFindRequest.searchablePublicationFromDate().year
        data["limitPublicationToYear"] = BookFindRequest.searchablePublicationToDate().year

        bookFindRequest.title?.let {
            data["title"] = it
        }
        data["publication_year_from"] = bookFindRequest.publication_year_from
        data["publication_year_from"] = bookFindRequest.publication_year_from
        data["publication_month_from"] = bookFindRequest.publication_month_from
        data["publication_year_to"] = bookFindRequest.publication_year_to
        data["publication_month_to"] = bookFindRequest.publication_month_to
        return data
    }

    /**
     * 書籍検索メソッド
     * @param BookFindRequest 書籍検索リクエストフォーム
     */
    private fun bookFind(title: String?, publicationFromDate: LocalDate, publicationToDate: LocalDate): Map<String, Any> {
        val data = HashMap<String, Any>()

        val books = bookService.findByTitlePublischer(title, publicationFromDate, publicationToDate)
        if (books.size == 0) {
            data["errors"] = listOf("書籍が見つかりませんでした。")
        } else {
            data["errors"] = emptyList<String>()
        }
        data["books"] = books
        return data
    }

    /**
     * エラー処理
     */
    @Error(exception = ConstraintViolationException::class)
    fun failed(
            request: HttpRequest<Map<String, Any>>,
            ex: ConstraintViolationException
    ): ModelAndView<*> {

        val responseMap = HashMap<String, Any>()
        responseMap["errors"] = ex.constraintViolations
                .map { constraintViolation ->
                    messageSource.getMessage(constraintViolation.messageTemplate,
                            MessageSource.MessageContext.DEFAULT,
                            constraintViolation.message)
                }.toList()

        val path = ex.constraintViolations.first()
                .propertyPath.toString()
                .split(".")[0]

        // propertyPathを元に遷移先画面を判定
        val view = when (path) {
            "find" -> {
                // パラメータを格納
                request.getBody(BookFindRequest::class.java).ifPresent({ bookFindRequest ->
                    println("requestSet:" + requestSet(bookFindRequest))
                    responseMap.putAll(requestSet(bookFindRequest))
                })
                "book/index"
            }
            else -> "book/index"
        }
        return ModelAndView(view, responseMap)
    }
}
