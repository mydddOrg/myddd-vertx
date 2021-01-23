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

    /**
     * 按时间倒序查询最近20条评论
     */
    suspend fun queryLatestComments(commentId:String):Future<List<Comment>>

}