package cc.lingenliu.comment.domain

import org.myddd.vertx.domain.BaseEntity
import org.myddd.vertx.ioc.InstanceFactory
import io.vertx.core.Future
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "comment")
class Comment : BaseEntity() {

    /**
     * 关联文章
     */
    @Column(name = "comment_id")
    lateinit var commentId:String

    /**
     * 关联评论根ID
     */
    @Column(name = "root_comment_id")
    var rootCommentId:Long = 0

    /**
     * 关联回复评论ID
     */
    @Column(name = "parent_comment_id")
    var parentCommentId:Long = 0

    /**
     * 昵称
     */
    var author:String? = null

    /**
     * 邮箱
     */
    var email:String? = null

    /**
     * 内容(Markdown格式)
     */
    lateinit var content:String

    companion object {
        val repository:CommentRepository by lazy { InstanceFactory.getInstance(CommentRepository::class.java) }
    }

    suspend fun createComment():Future<Comment> {
        return repository.createComment(this)
    }

    suspend fun createReplyComment(parentComment: Comment):Future<Comment> {
        return repository.createReplyComment(parentComment,this)
    }


}