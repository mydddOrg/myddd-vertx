package org.myddd.vertx.media

import org.myddd.vertx.base.BusinessLogicException

class MediaNotFoundException(values: Array<String> = emptyArray()):BusinessLogicException(MediaErrorCode.MEDIA_NOT_FOUND, values =  values)