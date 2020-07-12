package book.management.service

import book.management.dao.config.AuthorDao
import book.management.entity.AuthorEntity
import javax.inject.Singleton
import micronaut.session.dao.config.Transactional

/**
 * 著者サービス
 */
@Singleton
@Transactional
class AuthorService(private val authorDao: AuthorDao) {
    /**
     * @param name 著者名
     * @return 著者リスト
     */
    fun find(name: String?): List<AuthorEntity> {
        if (name == null) {
            return authorDao.findAll()
        } else {
            return authorDao.findByName(name)
        }
    }
}
