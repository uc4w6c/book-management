package book.management

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationException
import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.UserDetails
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.util.ArrayList
import javax.inject.Singleton
import org.reactivestreams.Publisher
import ses.db.service.LoginService
import ses.db.service.PasswordEncoderService

/**
 * 認証処理のためのクラス
 */
@Singleton
class AuthenticationProviderUserPassword(
    private val loginService: LoginService,
    private val passwordEncoder: PasswordEncoderService
) : AuthenticationProvider {

    /**
     * 認証処理
     */
    override fun authenticate(httpRequest: HttpRequest<*>?, authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> {
        return Flowable.create({ emitter ->
            val publisher = loginService.getPublisher(authenticationRequest.getIdentity().toString())
            val rawPassword = authenticationRequest.getSecret().toString()
            if (publisher?.password.equals(passwordEncoder.encode(rawPassword))) {
                val userDetails = UserDetails(publisher!!.id, ArrayList<String>())
                emitter.onNext(userDetails)
                emitter.onComplete()
            } else {
                emitter.onError(AuthenticationException(AuthenticationFailed()))
            }
        }, BackpressureStrategy.ERROR)
    }
}
