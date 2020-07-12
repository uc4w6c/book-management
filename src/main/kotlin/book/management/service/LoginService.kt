package ses.db.service

import javax.inject.Singleton
import micronaut.session.dao.config.Transactional
import ses.db.dao.config.PublisherDao
import ses.db.entity.PublisherEntity

/**
 * ログインサービス
 */
@Singleton
@Transactional
class LoginService(private val publisherDao: PublisherDao) {
    /**
     * @param id 出版社ID
     * @return 出版社エンティティ
     */
    fun getPublisher(id: String): PublisherEntity? {
        return publisherDao.findPublisher(id)
    }
}
