package book.management.service

import edu.umd.cs.findbugs.annotations.NonNull
// import org.springframework.security.crypto.password.NoOpPasswordEncoder
// import org.springframework.security.crypto.password.PasswordEncoder
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

/**
 * パスワード操作サービス
 */
@Singleton
open class NoOpPasswordEncoderService : PasswordEncoderService {

    /**
     * パスワードを暗号化せずそのまま返却
     * @param rawPassword プレーンパスワード
     * @return 暗号化済みのパスワードを返却(当クラスではプレーンなまま返却)
     */
    override fun encode(@NotBlank @NonNull rawPassword: String): String {
        return rawPassword
    }
}
