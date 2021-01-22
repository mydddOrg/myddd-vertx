package org.myddd.vertx.repository

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.impl.future.PromiseImpl
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.PoolOptions

import io.vertx.mysqlclient.MySQLConnectOptions
import org.myddd.vertx.ioc.InstanceFactory


abstract class AbstractRepository {

    companion object {
        var clientPool: MySQLPool

        private val vertx:Vertx by lazy { InstanceFactory.getInstance(Vertx::class.java) }

        private val config:JDBCRepositoryConfig by lazy { InstanceFactory.getInstance(JDBCRepositoryConfig::class.java) }

        init {
            val connectOptions = MySQLConnectOptions()
                .setPort(config.port)
                .setHost(config.host)
                .setDatabase(config.database)
                .setUser(config.username)
                .setPassword(config.password)
                .setConnectTimeout(5000)

            val poolOptions = PoolOptions()
                .setMaxSize(5)

            clientPool = MySQLPool.pool(vertx,connectOptions,poolOptions)
        }
    }

     fun isConnected():Future<Boolean> {
        val future = PromiseImpl<Boolean>()

        clientPool.query("select 1").execute{ar ->
            future.onSuccess(ar.succeeded())
        }

        return future
    }
}