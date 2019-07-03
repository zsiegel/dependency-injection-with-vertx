package com.zsiegel.api

import com.google.inject.Injector
import io.vertx.core.Verticle
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpServer
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.spi.VerticleFactory
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.awaitResult
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

private val logger = LoggerFactory.getLogger("Verticles")

fun options(eventLoops: Int = 2 * Runtime.getRuntime().availableProcessors()): VertxOptions {
    return VertxOptions()
        .setEventLoopPoolSize(eventLoops)
        .setWarningExceptionTime(1)
        .setMaxEventLoopExecuteTime(TimeUnit.SECONDS.toNanos(1))
        .setMaxWorkerExecuteTime(TimeUnit.SECONDS.toNanos(1))
        .setBlockedThreadCheckInterval(TimeUnit.SECONDS.toMillis(1))
}

class GuiceVerticleFactory(private val injector: Injector) : VerticleFactory {

    companion object {
        private const val prefix: String = "com.zsiegel"
    }

    override fun prefix(): String = prefix

    override fun createVerticle(verticleName: String, classLoader: ClassLoader): Verticle {
        val verticle = VerticleFactory.removePrefix(verticleName)
        return injector.getInstance(classLoader.loadClass(verticle)) as Verticle
    }
}

class HttpVerticle @Inject constructor(private val httpServer: HttpServer,
                                       private val router: Router,
                                       @Named("HOST") private val host: String,
                                       @Named("PORT") private val port: Int) : CoroutineVerticle() {

    @Throws(Exception::class)
    override suspend fun start() {
        awaitResult<HttpServer> {
            httpServer
                .requestHandler(router)
                .listen(port, host, it)

            LoggerFactory.getLogger(HttpVerticle::class.java).info("Server listening on $host:$port")
        }
    }
}