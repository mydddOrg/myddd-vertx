package cc.lingenliu.comment.domain

import io.vertx.core.Future

interface CommentRepository {

    fun addComment(comment:Comment): Future<Comment>?

    fun replyComment(comment: Comment,replyComment: Comment):Future<Comment>?

}