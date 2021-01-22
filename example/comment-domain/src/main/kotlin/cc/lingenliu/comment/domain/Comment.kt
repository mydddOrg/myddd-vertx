package cc.lingenliu.comment.domain

import io.vertx.core.Future

class Comment {

    private var id:Long = 0

    private lateinit var commentId:String

    private var author:String? = null

    private var email:String? = null

    private var content:String? = null

    private lateinit var rootId:String

    private lateinit var parentId:String

    companion object {
        private var repository:CommentRepository
            get() {
                TODO()
            }
            set(value) {}
    }

    /**
     * 新增一个评论
     */
    fun createComment(): Future<Comment>? {
        return repository.addComment(this)
    }

    /**
     * 回复一个评论
     */
    fun replayComment(comment: Comment):Future<Comment>? {
        return repository.replyComment(comment,this)
    }
}