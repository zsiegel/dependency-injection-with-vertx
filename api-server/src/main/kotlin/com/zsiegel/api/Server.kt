package com.zsiegel.api

import com.google.inject.Guice
import com.google.inject.Stage
import io.vertx.core.Vertx

fun main(args: Array<String>) {


    val vertx = Vertx.vertx(options())

    val injector = Guice.createInjector(Stage.PRODUCTION,
                                        ConfigModule(),
                                        RouterModule(vertx))

    val factory = GuiceVerticleFactory(injector)

    vertx.registerVerticleFactory(factory)
    vertx.deployVerticle("${factory.prefix()}:${HttpVerticle::class.java.name}")
}

