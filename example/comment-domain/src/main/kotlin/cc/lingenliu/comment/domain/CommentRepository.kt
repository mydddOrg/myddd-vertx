package cc.lingenliu.comment.domain

import io.vertx.core.Future

interface CommentRepository {

    /**
     * 新建一个评论
     */
    suspend fun createComment(comment: Comment):Future<Comment>

    /**
     * 新建一个回复评论
     */
    suspend fun createReplyComment(parentComment: Comment,replyComment:Comment):Future<Comment>


}