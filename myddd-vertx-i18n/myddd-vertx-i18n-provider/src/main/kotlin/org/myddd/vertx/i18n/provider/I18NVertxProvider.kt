package org.myddd.vertx.i18n.provider

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.await
import org.myddd.vertx.i18n.I18N
import java.util.*
import javax.inject.Inject

class I18NVertxProvider @Inject constructor(vertx:Vertx) : I18N {

    companion object {
        const val NULL_LANGUAGE = "NULL"
        const val EMPTY = ""
        val logger: Logger = LoggerFactory.getLogger(I18NVertxProvider::class.java)
    }

    private val vertx:Vertx = vertx

    private val i18nConfig:MutableMap<String, JsonObject?> = mutableMapOf()

    override suspend fun getMessage(key: String, params: Array<String>,language:String?): Future<String> {
        val promise = PromiseImpl<String>()
        val languageJsonObject = getLanguageJsonObject(language).await()

        languageJsonObject?.let {
            val i18nValue = it.getString(key)
            if(i18nValue.isNullOrEmpty()){
                promise.onSuccess(EMPTY)
            }else{
                promise.onSuccess(String.format(i18nValue,*params))
            }
            return@let
        }

        promise.onSuccess(EMPTY)
        return promise.future()
    }

    private suspend fun getLanguageJsonObject(language: String?): Future<JsonObject?> {
        val promise = PromiseImpl<JsonObject?>()
        val exceptedLanguage:String = if(language.isNullOrEmpty()) NULL_LANGUAGE else language

        if(i18nConfig.containsKey(exceptedLanguage)){
            promise.onSuccess(i18nConfig[exceptedLanguage])
        }else{
            var jsonObject: JsonObject? = loadI18NConfig(exceptedLanguage).await()
            i18nConfig[exceptedLanguage] = jsonObject
            promise.onSuccess(i18nConfig[exceptedLanguage])
        }
        return promise
    }

    private fun loadI18NConfig(language: String?): Future<JsonObject?> {
        val path = if(language.isNullOrEmpty() || language == NULL_LANGUAGE) "META-INF/i18n/default.properties" else "META-INF/i18n/default_$language.properties"

        val promise = PromiseImpl<JsonObject?>()

        val fileStore = ConfigStoreOptions()
            .setType("file")
            .setFormat("properties")
            .setConfig(JsonObject().put("path", path))

        val options = ConfigRetrieverOptions()
            .addStore(fileStore)
        val configRetriever = ConfigRetriever.create(vertx, options)

        configRetriever.getConfig {
            if(it.succeeded()){
                promise.onSuccess(it.result())
            }else{
                promise.fail(it.cause())
            }
        }

        return promise
    }

}