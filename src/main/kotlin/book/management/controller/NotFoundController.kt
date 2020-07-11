package book.management.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.views.ViewsRenderer

/**
 * NotFoundエラーコントローラ
 */
@Controller("/notfound")
class NotFoundController(private val viewsRenderer: ViewsRenderer) {
    @Error(status = HttpStatus.NOT_FOUND, global = true)
    fun notFound(request: HttpRequest<*>): HttpResponse<*> {
        return HttpResponse.ok(viewsRenderer.render("notFound", emptyMap<Any, Any>()))
                    .contentType(MediaType.TEXT_HTML)
    }
}
