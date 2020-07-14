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
     * 全ての著者を取得
     * @return 著者リスト
     */
    fun findAll(): List<AuthorEntity> {
        return authorDao.findAll()
    }

    /**
     * 名前で著者を検索
     * @param name 著者名
     * @return 著者リスト
     */
    fun findById(id: Long): AuthorEntity? {
        return authorDao.findById(id)
    }

    /**
     * 名前で著者を検索
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

    /**
     * 著者を登録
     * @param AuthorEntity 著者エンティティ
     * @return AuthorEntity 著者エンティティ
     */
    fun regist(authorEntity: AuthorEntity): AuthorEntity {
        return authorDao.insert(authorEntity).getEntity()
    }

    /**
     * 著者を更新
     * @param AuthorEntity 著者エンティティ
     * @return AuthorEntity 著者エンティティ
     */
    fun update(authorEntity: AuthorEntity): AuthorEntity {
        return authorDao.update(authorEntity).getEntity()
    }
}
