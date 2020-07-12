package book.management.dao.config

import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import java.util.function.Supplier
import javax.inject.Singleton
import org.seasar.doma.jdbc.tx.LocalTransactionManager

@Singleton
class TransactionInterceptor(private val transactionManager: LocalTransactionManager) : MethodInterceptor<Any?, Any?> {
    override fun intercept(context: MethodInvocationContext<Any?, Any?>): Any? {
        return transactionManager.required(Supplier { context.proceed() })
    }
}
