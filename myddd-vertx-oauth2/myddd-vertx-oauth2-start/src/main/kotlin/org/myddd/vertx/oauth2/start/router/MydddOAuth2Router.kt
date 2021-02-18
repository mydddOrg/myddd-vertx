package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.oauth2.api.OAuth2Application

class MydddOAuth2Router constructor(router: Router) {

    var router:Router = router

    val oauth2Application:OAuth2Application by lazy { InstanceFactory.getInstance(OAuth2Application::class.java)}

    init {
        helloRouter()
    }

    private fun helloRouter(){
        router.route("/hello").respond {
            it.bodyAsJson
            Future.succeededFuture(JsonObject().put("hello", "world"))
        }
    }
}