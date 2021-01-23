package cc.lingenliu.comment.domain

import io.vertx.core.Future
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.repository.hibernate.EntityRepositoryHibernate

class CommentRepositoryHibernate : EntityRepositoryHibernate(),CommentRepository {

    override suspend fun createComment(comment: Comment): Future<Comment> {
        val future = PromiseImpl<Comment>()
        try {
            require(comment.id == 0L)
            comment.created = System.currentTimeMillis()
            var created =  save(comment).await()
            created.rootCommentId = created.id
            created = save(created).await()
            future.onSuccess(created)
        }catch (e:Exception){
            e.printStackTrace()
            future.fail(e)
        }
        return future
    }

    override suspend fun createReplyComment(parentComment: Comment, replyComment: Comment): Future<Comment> {
        val future = PromiseImpl<Comment>()

        try {
            require(parentComment.id > 0)
            replyComment.parentCommentId = parentComment.id
            replyComment.rootCommentId = parentComment.rootCommentId
            replyComment.created = System.currentTimeMillis()

            val created =  save(replyComment).await()
            future.onSuccess(created)
        }catch (e:Exception){
            e.printStackTrace()
            future.fail(e)
        }
        return future

    }

}