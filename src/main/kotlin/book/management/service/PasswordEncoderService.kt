package book.management.service

import edu.umd.cs.findbugs.annotations.NonNull
import javax.validation.constraints.NotBlank

/**
 * パスワードエンコードサービス
 */
interface PasswordEncoderService {
    /**
     * 指定したパスワードを暗号化する
     * @param rawPassword プレーンパスワード
     * @return 暗号化済みのパスワードを返却
     */
    fun encode(@NotBlank @NonNull rawPassword: String): String
}
