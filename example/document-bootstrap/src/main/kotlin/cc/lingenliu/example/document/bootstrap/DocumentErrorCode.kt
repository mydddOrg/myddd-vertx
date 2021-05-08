package cc.lingenliu.example.document.bootstrap

import org.myddd.vertx.error.ErrorCode

enum class DocumentErrorCode:ErrorCode {

    MEDIA_NOT_FOUND {
        override fun errorStatus(): Int {
            return 1000
        }
    }
}