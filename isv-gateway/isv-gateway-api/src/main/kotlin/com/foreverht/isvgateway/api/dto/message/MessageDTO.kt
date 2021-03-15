package com.foreverht.isvgateway.api.dto.message

data class MessageDTO(
    var toUserList:List<String> = emptyList(),
    var toOrgList:List<String> = emptyList(),
    var forAll:Boolean,
    var platforms:List<String> = arrayListOf("ANDROID","IOS","PC"),
    var body:AbstractMessageBody
    )
