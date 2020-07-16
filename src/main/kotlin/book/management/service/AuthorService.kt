package book.management.service

import book.management.dao.config.AuthorDao
import book.management.dao.config.BookDao
import book.management.dao.config.Transactional
import book.management.entity.AuthorEntity
import book.management.exception.PublisherPermissionException
import javax.inject.Singleton

/**
 * 著者サービス
 */
@Singleton
@Transactional
class AuthorService(private val authorDao: AuthorDao, private val bookDao: BookDao) {
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
     * 書籍IDで著者一覧を取得
     * @param id 書籍ID
     * @return 書籍
     */
    fun findByBookId(id: Long): List<AuthorEntity> {
        return authorDao.findByBookId(id)
    }

    /**
     * 著者IDリストから指定した出版社のみで出版している著者を取得
     * @param publisherId 出版社ID
     * @param authorIdList 著者IDリスト
     * @return 指定出版社のみで出版している著者IDセット
     */
    fun uniquePublisherAuthorFindByIdList(publisherId: String, authorIdList: List<Long>): Set<Long> {
        val authorPublishersEntityList = authorDao.findAuthorPublishersByIdList(authorIdList)

        // 他の出版社で出している著者のセット
        val authorHaveOtherPublisher = authorPublishersEntityList
                .filter { authorPublishersEntity ->
                    if (authorPublishersEntity.publisherId == null) false
                    else authorPublishersEntity.publisherId!! != publisherId
                }.map { it.authorId }
                .distinct().toSet()

        // 指定した出版社のみで出版している著者を抽出
        return authorPublishersEntityList
                .filter { authorPublishersEntity ->
                    !authorHaveOtherPublisher.contains(authorPublishersEntity.authorId)
                }.map { it.authorId }
                .toSet()
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

    /**
     * 著者を削除
     * @param publisherId 出版社ID
     * @param List<Long> 削除対象著者IDリスト
     */
    fun delete(publisherId: String, authorIdList: List<Long>) {
        val deletableAuthorSet = this.uniquePublisherAuthorFindByIdList(publisherId, authorIdList)
        if (authorIdList.any { authorId -> !deletableAuthorSet.contains(authorId) })
            throw PublisherPermissionException("著者の削除ができませんでした。")

        val bookAuthorsEntityList = bookDao.findBookAuthorsEntityByAuthorId(authorIdList).map { it.bookId }
        bookDao.deleteBookAuthorsEntityByBookIdList(bookAuthorsEntityList)
        bookDao.deleteByBookIdList(bookAuthorsEntityList)
        authorDao.deleteByAuthorIdList(authorIdList)
    }
}
