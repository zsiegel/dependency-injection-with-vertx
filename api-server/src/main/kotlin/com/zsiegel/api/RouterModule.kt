package com.zsiegel.api

import com.google.inject.AbstractModule
import com.google.inject.Exposed
import com.google.inject.PrivateModule
import com.google.inject.Provides
import com.google.inject.name.Names
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import java.util.Properties
import javax.inject.Singleton

internal class ConfigModule : AbstractModule() {
    override fun configure() {
        val properties = Properties()
        properties.setProperty("HOST", System.getenv("HOST"))
        properties.setProperty("PORT", System.getenv("PORT"))
        Names.bindProperties(binder(), properties)
    }
}

internal class RouterModule(private val vertx: Vertx) : PrivateModule() {
    override fun configure() {
    }

    @Provides
    @Singleton
    @Exposed
    fun httpServer(): HttpServer {
        val options = HttpServerOptions()
        options.isCompressionSupported = true
        return vertx.createHttpServer(options)
    }

    @Provides
    @Singleton
    @Exposed
    fun router(): Router {
        val router = Router.router(vertx)

        router.route("/").handler { ctx ->
            LoggerFactory.getLogger("Router").debug("Received request on /")
            ctx.response()
                .setStatusCode(200)
                .end("Dependency Injection with Vert.x")
        }

        return router
    }
}