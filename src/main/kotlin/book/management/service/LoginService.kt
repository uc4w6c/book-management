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
     * @param id 出版社IDを指定
     * @return 出版社エンティティを返却
     */
    fun getPublisher(id: String): PublisherEntity? {
        return publisherDao.findPublisher(id)
    }
}
