package org.myddd.vertx.media.domain

import org.myddd.vertx.error.ErrorCode

enum class MediaErrorCode:ErrorCode {
    SOURCE_FILE_NOT_EXISTS,

    MEDIA_NOT_FOUND
}