package org.myddd.vertx.ioc

interface InstanceProvider {

    fun <T> getInstance(beanType: Class<T>?): T

    fun <T> getInstance(beanType: Class<T>?, beanName: String?): T


}