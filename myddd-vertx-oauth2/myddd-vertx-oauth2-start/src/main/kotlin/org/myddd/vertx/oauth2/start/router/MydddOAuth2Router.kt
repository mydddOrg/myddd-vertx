package org.myddd.vertx.oauth2.start.router

import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class MydddOAuth2Router constructor(router: Router) {

    var router:Router = router

    init {
        helloRouter()
    }

    private fun helloRouter(){
        router.route("/hello").respond {
            Future.succeededFuture(JsonObject().put("hello", "world"))
        }
    }

}