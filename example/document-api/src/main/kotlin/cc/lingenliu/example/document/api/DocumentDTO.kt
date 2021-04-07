package cc.lingenliu.example.document.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class DocumentDTO @JsonCreator constructor(
    @JsonProperty(value = "id")
    val id:Long = 0,
    @JsonProperty(value = "mediaId")
    val mediaId:String,
    @JsonProperty(value = "name")
    val name:String,
    @JsonProperty(value = "documentType")
    val documentType:String,
    @JsonProperty(value = "md5")
    val md5:String,
    @JsonProperty(value = "suffix")
    val suffix:String)
