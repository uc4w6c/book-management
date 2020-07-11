package ses.db.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View

/**
 * ホームコントローラ
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
class HomeController() {

    @Produces(MediaType.TEXT_HTML)
    @Get("/")
    @View("home")
    fun index() { }
}
