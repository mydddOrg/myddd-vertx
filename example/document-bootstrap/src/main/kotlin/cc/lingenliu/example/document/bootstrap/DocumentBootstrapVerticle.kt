package cc.lingenliu.example.document.bootstrap

import cc.lingenliu.example.document.bootstrap.route.DocumentRoute
import com.google.inject.AbstractModule
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.myddd.vertx.web.router.BootstrapVerticle

class DocumentBootstrapVerticle(port:Int = 8080) : BootstrapVerticle(port = port) {

    override fun abstractModules(vertx: Vertx): AbstractModule {
        return DocumentGuice(vertx)
    }

    override fun routers(vertx: Vertx, router: Router): () -> Unit {
        return {
            DocumentRoute(vertx,router)
        }
    }

}