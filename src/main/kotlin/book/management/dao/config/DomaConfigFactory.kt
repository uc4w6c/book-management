package micronaut.session.dao.config

import io.micronaut.context.annotation.Factory
import javax.inject.Singleton
import javax.sql.DataSource
import org.seasar.doma.jdbc.ConfigSupport
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource
import org.seasar.doma.jdbc.tx.LocalTransactionManager

@Factory
class DomaConfigFactory {

    @Singleton
    fun localTransactionDataSource(dataSource: DataSource): LocalTransactionDataSource {
        return LocalTransactionDataSource(dataSource)
    }

    @Singleton
    fun localTransactionManager(dataSource: LocalTransactionDataSource): LocalTransactionManager {
        return LocalTransactionManager(
                dataSource.getLocalTransaction(ConfigSupport.defaultJdbcLogger))
    }
}
