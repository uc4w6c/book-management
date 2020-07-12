package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.Transactional
import book.management.entity.AuthorEntity
import javax.inject.Singleton

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
