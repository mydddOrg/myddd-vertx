package cc.lingenliu.comment.domain

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.myddd.vertx.ioc.InstanceFactory
import java.util.*

class TestCommentRepository : AbstractTest() {

    private val commentRepository = InstanceFactory.getInstance(CommentRepository::class.java)

    private fun createComment():Comment {
        val comment = Comment()
        comment.commentId = UUID.randomUUID().toString()
        comment.author = "lingen"
        comment.email = "lingen.liu@gmail.com"
        comment.content = "Hello"
        return comment
    }

    private fun createReplyComment():Comment {
        val comment = Comment()
        comment.commentId = UUID.randomUUID().toString()
        comment.author = "lingen"
        comment.email = "lingen.liu@gmail.com"
        comment.content = "Hello 2"
        return comment
    }

    @Test
    fun testAddComment(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try {
                val comment = createComment()
                val created = commentRepository.createComment(comment).await()
                testContext.verify {
                    Assertions.assertTrue(created.id > 0)
                    Assertions.assertEquals(created.id,created.rootCommentId)
                    Assertions.assertEquals(created.level,0)
                }

                testContext.completeNow()
            }catch (e:Exception){
                testContext.failNow(e)
            }
        }
    }

    @Test
    fun testReplyComment(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            try{
                val comment = createComment()
                val created = commentRepository.createComment(comment).await()
                var replyComment = createReplyComment()
                val createdReplyComment = commentRepository.createReplyComment(created,replyComment).await()

                testContext.verify {
                    Assertions.assertTrue(createdReplyComment.id > 0)
                    Assertions.assertEquals(createdReplyComment.rootCommentId,created.rootCommentId)
                    Assertions.assertEquals(createdReplyComment.parentCommentId,created.id)
                    Assertions.assertEquals(replyComment.level,created.level  + 1)
                }

                testContext.completeNow()
            }catch (e:Exception) {
                testContext.failNow(e)
            }
        }
    }

    @Test
    fun testQueryLatestComments(vertx: Vertx, testContext: VertxTestContext){
        GlobalScope.launch {
            val comment = createComment()
            val created = commentRepository.createComment(comment).await()
            Assertions.assertNotNull(created)

            var list = commentRepository.queryLatestComments(created.commentId).await()

            testContext.verify {
                Assertions.assertTrue(list.isNotEmpty())
            }

            testContext.completeNow()
        }
    }
}