package org.myddd.vertx.oauth2.application

import org.myddd.vertx.oauth2.api.OAuth2ClientDTO
import org.myddd.vertx.oauth2.api.OAuth2TokenDTO
import org.myddd.vertx.oauth2.api.OAuth2UserDTO
import org.myddd.vertx.oauth2.domain.OAuth2Client
import org.myddd.vertx.oauth2.domain.OAuth2Token


fun toOAuth2UserDTO(client: OAuth2Client,token: OAuth2Token):OAuth2UserDTO {
    val userDTO = OAuth2UserDTO()
    userDTO.clientId = client.clientId
    userDTO.clientName = client.name
    userDTO.tokenDTO = OAuth2TokenDTO(accessToken = token.accessToken,accessExpiredIn = token.accessExpiredIn,refreshToken = token.refreshToken,refreshExpiredIn = token.refreshExpiredIn)
    return userDTO
}

fun toOAuth2ClientDTO(client: OAuth2Client):OAuth2ClientDTO{
    return OAuth2ClientDTO(id = client.id,version = client.version,clientId = client.clientId,clientSecret = client.clientSecret,description = client.description,name = client.name,disabled = client.disabled)
}

fun toOAuth2Client(clientDTO: OAuth2ClientDTO):OAuth2Client {
    val oAuth2Client = OAuth2Client()
    clientDTO.id?.also { oAuth2Client.id = it!! }
    clientDTO.version?.also { oAuth2Client.version = it!! }
    oAuth2Client.clientId = clientDTO.clientId
    oAuth2Client.name = clientDTO.name
    clientDTO.clientSecret?.also { oAuth2Client.clientSecret = it!! }
    oAuth2Client.disabled = clientDTO.disabled
    return oAuth2Client
}