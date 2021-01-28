package org.myddd.vertx.oauth2.application

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