package book.management.controller

import book.management.controller.request.AuthorRequest
import book.management.service.AuthorService
import io.micronaut.context.MessageSource
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body as Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.session.Session
import io.micronaut.validation.Validated
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import java.util.HashMap
import javax.validation.ConstraintViolationException
import javax.validation.Valid

/**
 * 著者操作コントローラ
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/author")
@Validated
class AuthorController(
    private val messageSource: MessageSource,
    private val authorService: AuthorService
) {

    /**
     * 著者検索
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/")
    @View("author/index")
    fun find(session: Session, @QueryValue name: String?): Map<String, Any> {
        val authorList = authorService.find(name)
        val data = HashMap<String, Any>()
        if (authorList.size == 0) {
            data["errors"] = listOf("著者が見つかりませんでした。")
        } else {
            data["errors"] = emptyList<String>()
        }

        data["info"] = session.get("info").orElse("false")
        if (session.contains("info")) session.put("info", "")
        data["authors"] = authorList
        return data
    }

    /**
     * 著者登録画面表示
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/regist")
    @View("author/regist")
    fun registDisp() { }

    /**
     * 著者登録画面表示
     */
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Post("/regist")
    @View("author/index")
    fun regist(session: Session, @Body @Valid authorRequest: AuthorRequest): Map<String, Any> {
        authorService.regist(authorRequest.toEntity())
        session.put("info", "著者を登録しました。")

        return find(session, null)
    }

    /**
     * エラー処理
     */
    @Error(exception = ConstraintViolationException::class)
    fun registFailed(
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
            "regist" -> {
                // パラメータを格納
                request.getBody(AuthorRequest::class.java).ifPresent({ authorRequest ->
                    // responseMap.put("authorRequest", authorRequest)
                    responseMap.put("name", authorRequest.name)
                    authorRequest.profile?.let {
                        responseMap.put("profile", it)
                    }
                })
                "author/regist"
            }
            else -> "author/index"
        }
        return ModelAndView(view, responseMap)
    }
}
