package book.management.service

import book.management.config.PublisherDao
import book.management.dao.config.Transactional
import book.management.entity.PublisherEntity
import javax.inject.Singleton

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
