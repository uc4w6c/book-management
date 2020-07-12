package book.management.controller

import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Error
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authenticator
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.event.LoginFailedEvent
import io.micronaut.security.event.LoginSuccessfulEvent
import io.micronaut.security.handlers.LoginHandler
import io.micronaut.security.rules.SecurityRule
import io.micronaut.validation.Validated
import io.micronaut.views.View
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.Collections
import java.util.HashMap
import javax.validation.ConstraintViolationException
import javax.validation.Valid

/**
 * ログインコントローラ
 */
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/login")
@Validated
public class LoginAuthController(
    private val authenticator: Authenticator,
    private val loginHandler: LoginHandler,
    private val eventPublisher: ApplicationEventPublisher
) {

    /**
     * ログイン画面初期表示
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/")
    @View("auth")
    fun auth(): Map<String, Any> {
        return HashMap()
    }

    /**
     * ログイン処理
     */
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON)
    @Post
    fun login(@Valid @Body usernamePasswordCredentials: UsernamePasswordCredentials, request: HttpRequest<*>): Single<MutableHttpResponse<*>> {
        val authenticationResponseFlowable = Flowable.fromPublisher(authenticator.authenticate(request, usernamePasswordCredentials))

        return authenticationResponseFlowable.map { authenticationResponse ->
            if (authenticationResponse.isAuthenticated && authenticationResponse.userDetails.isPresent) {
                val userDetails = authenticationResponse.userDetails.get()
                eventPublisher.publishEvent(LoginSuccessfulEvent(userDetails))
                loginHandler.loginSuccess(userDetails, request)
            } else {
                eventPublisher.publishEvent(LoginFailedEvent(authenticationResponse))
                loginHandler.loginFailed(authenticationResponse, request)
            }
        }.first(HttpResponse.status<Unit>(HttpStatus.UNAUTHORIZED))
    }

    /**
     * ログイン処理失敗処理
     */
    @Produces(MediaType.TEXT_HTML)
    @Get("/authFailed")
    @View("auth")
    fun authFailed(): Map<String, Any> {
        return Collections.singletonMap<String, Any>("errors", true)
    }

    /**
     * ログイン処理Validationエラー処理
     */
    @View("auth")
    @Error(exception = ConstraintViolationException::class)
    fun initFailed(
        request: HttpRequest<Map<String, Any>>,
        ex: ConstraintViolationException
    ): Map<String, Any> {
        return Collections.singletonMap<String, Any>("errors", true)
    }
}
