package org.myddd.vertx.ioc

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await

class InstanceFactory private constructor(){

    companion object {
        private var instanceProvider: InstanceProvider? = null

        suspend fun setInstanceProvider(vertx: Vertx, instanceProvider:InstanceProvider):Future<Void>{
            return try {
                vertx.executeBlocking<Void> {
                    setInstanceProvider(instanceProvider)
                    it.complete()
                }.await()
                Future.succeededFuture()
            }catch (t:Throwable){
                Future.failedFuture(t)
            }
        }

        fun setInstanceProvider(instanceProvider:InstanceProvider){
            InstanceFactory.instanceProvider = instanceProvider
        }

        fun <T> getInstance(beanType: Class<T>?): T {
            requireNotNull(instanceProvider)
            return instanceProvider!!.getInstance(beanType)
        }

        fun <T> getInstance(beanType: Class<T>?, beanName: String?): T {
            requireNotNull(instanceProvider)
            return instanceProvider!!.getInstance(beanType,beanName)
        }
    }
}