package org.myddd.vertx.cache

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import io.vertx.core.Vertx
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.myddd.vertx.ioc.InstanceFactory
import org.myddd.vertx.ioc.guice.GuiceInstanceProvider


class IOCInitExtension:BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        InstanceFactory.setInstanceProvider(GuiceInstanceProvider(Guice.createInjector(object : AbstractModule(){
            override fun configure() {
                bind(Vertx::class.java).toInstance(Vertx.vertx())
                bind(Cache::class.java).annotatedWith(Names.named("Cache")).toInstance(ShareDataCache<Entity>(name = "Cache"))
                bind(Cache::class.java).annotatedWith(Names.named("AsyncCache")).toInstance(ShareDataCache<Entity>(name = "AsyncCache",localCache = false))

                bind(Cache::class.java).annotatedWith(Names.named("AnotherCache")).toInstance(ShareDataCache<Entity>(name = "AnotherCache"))
            }
        })))
    }
}