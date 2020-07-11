package book.management.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.views.ViewsRenderer

/**
 * アプリケーションエラーコントローラ
 */
@Controller("/apperror")
class ApplicationErrorController(private val viewsRenderer: ViewsRenderer) {
    @Error(status = HttpStatus.INTERNAL_SERVER_ERROR, global = true)
    fun notFound(request: HttpRequest<*>): HttpResponse<*> {
        return HttpResponse.ok(viewsRenderer.render("appError", emptyMap<Any, Any>()))
                    .contentType(MediaType.TEXT_HTML)
    }
}
