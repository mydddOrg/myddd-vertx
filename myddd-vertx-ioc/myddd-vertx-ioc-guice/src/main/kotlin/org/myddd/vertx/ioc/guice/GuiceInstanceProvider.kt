package org.myddd.vertx.ioc.guice

import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.name.Names
import org.myddd.vertx.ioc.InstanceProvider

class GuiceInstanceProvider(injector: Injector?): InstanceProvider {

    private var injector: Injector? = null

    init {
        this.injector = injector
        this.injector
    }

    override fun <T> getInstance(beanType: Class<T>?): T {
        requireNotNull(injector)
        return injector!!.getInstance(beanType)
    }

    override fun <T> getInstance(beanType: Class<T>?, beanName: String?): T {
        requireNotNull(injector)
        return injector!!.getInstance(Key.get(beanType,Names.named(beanName)))
    }

}