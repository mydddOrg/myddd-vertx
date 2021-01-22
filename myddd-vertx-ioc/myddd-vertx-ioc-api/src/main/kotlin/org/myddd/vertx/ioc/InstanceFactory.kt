package org.myddd.vertx.ioc

class InstanceFactory {

    companion object {
        private var instanceProvider: InstanceProvider? = null

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