package cc.lingenliu.example.document.bootstrap

import cc.lingenliu.example.document.api.DocumentApplication
import cc.lingenliu.example.document.application.DocumentApplicationProvider
import cc.lingenliu.example.document.domain.DocumentRepository
import cc.lingenliu.example.document.domain.DocumentRepositoryHibernate
import io.vertx.core.Vertx

class DocumentGuice(vertx: Vertx) : AbstractWebModule(vertx = vertx) {

    override fun configure() {
        super.configure()

        bind(DocumentRepository::class.java).to(DocumentRepositoryHibernate::class.java)
        bind(DocumentApplication::class.java).to(DocumentApplicationProvider::class.java)
    }
}