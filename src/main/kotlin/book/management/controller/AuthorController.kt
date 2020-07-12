package book.management.controller

import book.management.entity.AuthorEntity
import book.management.service.AuthorService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View
import java.util.HashMap

/**
 * 著者操作コントローラ
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/author")
class AuthorController(private val authorService: AuthorService) {

    @Produces(MediaType.TEXT_HTML)
    @Get("/")
    @View("author/index")
    fun find(@QueryValue name: String?): Map<String, Any> {
        val authorList = authorService.find(name)
        val data = HashMap<String, Any>()
        if (authorList.size == 0) {
            data["errors"] = listOf("著者が見つかりませんでした。")
        } else {
            data["errors"] = emptyList<String>()
        }
        data["authors"] = authorList
        return data
    }
}
