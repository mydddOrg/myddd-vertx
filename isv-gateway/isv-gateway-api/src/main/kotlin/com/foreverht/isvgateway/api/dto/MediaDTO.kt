package com.foreverht.isvgateway.api.dto

data class MediaDTO(val mediaId:String,val destPath:String,val contentType:String ="application/octet-stream",val size:Long,val contentDisposition:String)