package book.management.controller

import book.management.controller.request.author.AuthorRegistRequest
import book.management.controller.request.author.AuthorUpdateRequest
import book.management.controller.request.book.BookFindRequest
import book.management.controller.request.book.BookRegistRequest
import book.management.controller.request.book.BookUpdateRequest
import book.management.exception.DataNotFoundException
import book.management.exception.NotUpdatableException
import book.management.service.AuthorService
import book.management.service.BookService
import book.management.utils.getFirstDayOfMonth
import book.management.utils.getLastDayOfMonth
import io.micronaut.context.MessageSource
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.annotation.Body as Body
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.session.Session
import io.micronaut.validation.Validated
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import java.security.Principal
import java.time.LocalDate
import java.util.HashMap
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * 書籍操作コントローラ
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/book")
open class BookController(
    private val messageSource: MessageSource,
    private val bookService: BookService,
    private val authorService: AuthorService
) {

    /**
     * 書籍一覧初期表示
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/")
    @View("book/index")
    fun index(principal: Principal?, session: Session): Map<String, Any> {
        val data = HashMap<String, Any>()

        val defaultBookFind = BookFindRequest.defaultBuild()

        data.putAll(bookFind(defaultBookFind.title, defaultBookFind.getPublicationFromDate(), defaultBookFind.getPublicationToDate()))
        data.putAll(requestSet(defaultBookFind))

        data["publisherId"] = principal!!.name
        data["info"] = session.get("info").orElse("false")
        if (session.contains("info")) session.put("info", "")

        return data
    }

    /**
     * 書籍検索
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/find{?bookFindRequest*}")
    @View("book/index")
    open fun find(principal: Principal?, @Valid /*@RequestBean*/ bookFindRequest: BookFindRequest): Map<String, Any> {
        val data = HashMap<String, Any>()

        data.putAll(requestSet(bookFindRequest))

        if (!bookFindRequest.isValid()) {
            data.put("errors", bookFindRequest.getErrorMessages())
            return data
        }

        data.putAll(bookFind(bookFindRequest.title, bookFindRequest.getPublicationFromDate(), bookFindRequest.getPublicationToDate()))
        data["publisherId"] = principal!!.name
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
     * 書籍登録画面表示
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/regist")
    @View("book/regist")
    fun registDisp(): Map<String, Any> {
        val responseMap = HashMap<String, Any>()
        val authors = authorService.findAll()
        responseMap.put("selectarable_author_list", authors)
        return responseMap
    }

    /**
     * 書籍登録
     */
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/regist")
    @View("book/index")
    open fun regist(principal: Principal?, session: Session, @Body @Valid bookRequest: BookRegistRequest): Map<String, Any> {
        val publisherId = principal!!.name
        val book = bookService.regist(bookRequest.toEntity(publisherId), bookRequest.author_id_list)
        session.put("info", "書籍を登録しました。")

        return index(principal, session)
    }

    /**
     * 書籍登録のパラメータセット
     * @param BookRegistRequest
     * @return 書籍登録のセットされたパラメータ
     */
    fun registRequestSet(bookRegistRequest: BookRegistRequest): Map<String, Any> {
        val data = HashMap<String, Any>()
        bookRegistRequest.title?.let {
            data.put("title", it)
        }
        data.put("publication_date", bookRegistRequest.publication_date)
        data.put("summary", bookRegistRequest.summary)
        data.put("author_id_list", bookRegistRequest.author_id_list)
        return data
    }

    /**
     * 書籍更新画面表示
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("{id}/update")
    @View("book/update")
    fun updateDisp(@QueryValue("id") id: Long): ModelAndView<*> {
        val responseMap = HashMap<String, Any>()
        var view: String

        val book = bookService.findById(id)
        if (book == null) {
            view = "notFound"
        } else {
            view = "book/update"
            responseMap.put("id", book.id!!)
            book.title?.let {
                responseMap.put("title", it)
            }
            responseMap.put("publication_date", book.publicationDate)
            responseMap.put("summary", book.summary)
        }

        val selectableAuthors = authorService.findAll()
        responseMap.put("selectarable_author_list", selectableAuthors)
        val authors = authorService.findByBookId(id)
        responseMap.put("author_id_list", authors.map { it.id })
        return ModelAndView(view, responseMap)
    }

    /**
     * 書籍更新
     */
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/update")
    @View("book/index")
    open fun update(principal: Principal?, session: Session, @Body @Valid bookUpdateRequest: BookUpdateRequest): Map<String, Any> {
        val publisherId = principal!!.name
        bookService.update(publisherId, bookUpdateRequest.toEntity(publisherId), bookUpdateRequest.author_id_list)
        session.put("info", "書籍を更新しました。")

        return index(principal, session)
    }

    /**
     * 書籍更新のパラメータセット
     * @param BookRegistRequest
     * @return 書籍登録のセットされたパラメータ
     */
    fun updateRequestSet(bookUpdateRequest: BookUpdateRequest): Map<String, Any> {
        val data = HashMap<String, Any>()
        bookUpdateRequest.id?.let {
            data.put("id", it)
        }
        bookUpdateRequest.title?.let {
            data.put("title", it)
        }
        data.put("publication_date", bookUpdateRequest.publication_date)
        data.put("summary", bookUpdateRequest.summary)
        data.put("author_id_list", bookUpdateRequest.author_id_list)
        return data
    }

    /**
     * 更新時エラー処理
     */
    // TODO: 遷移しない。要修正
    @Error(exception = NotUpdatableException::class)
    fun updateFailed(
            request: HttpRequest<Map<String, Any>>,
            ex: NotUpdatableException
    ): ModelAndView<*> {
        val responseMap = HashMap<String, Any>()
        responseMap["errors"] = listOf(ex.message)

        request.getBody(BookUpdateRequest::class.java).ifPresent({ bookUpdateRequest ->
            responseMap.putAll(updateRequestSet(bookUpdateRequest))
        })

        return ModelAndView("book/update", responseMap)
    }

    /**
     *
     */
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/delete")
    @View("book/index")
    open fun delete(
            principal: Principal?,
            session: Session,
            @Body("book_id_list") @NotNull bookDeleteIdValue: String
    ): Map<String, Any> {
        val bookDeleteIdList = bookDeleteIdValue.split(',').map(String::toLong)
        val publisherId = principal!!.name
        bookService.delete(publisherId, bookDeleteIdList)
        session.put("info", "書籍を削除しました。")

        return index(principal, session)
    }

    /**
     * Validationエラー処理
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
            "regist" -> {
                request.getBody(BookRegistRequest::class.java).ifPresent({ bookRegistRequest ->
                    responseMap.putAll(registRequestSet(bookRegistRequest))
                })
                "book/regist"
            }
            "update" -> {
                request.getBody(BookUpdateRequest::class.java).ifPresent({ bookUpdateRequest ->
                    responseMap.putAll(updateRequestSet(bookUpdateRequest))
                })
                "book/update"
            }
            else -> "book/index"
        }
        return ModelAndView(view, responseMap)
    }
}
