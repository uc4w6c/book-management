package book.management

import io.micronaut.context.MessageSource
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.i18n.ResourceBundleMessageSource
import java.util.Locale

/**
 * 共通設定Bean Factory
 */
@Factory
class AppConfig {

    /**
     * MessageSourceBeanの作成
     * Validationの日本語化のためにMessages.propertiesを読み込む
     */
    @Bean
    fun messageSource(): MessageSource {
        return ResourceBundleMessageSource("i18n.Messages", Locale.JAPAN)
    }
}
