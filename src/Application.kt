package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        //get data note berdasarkan id
        get("/note/{id}") {
            val id = call.parameters["id"]
            call.respond("${id}")
        }
        get("/note") {
            val id = call.request.queryParameters["id"]
            call.respond("${id}")
        }
        route("/notes") {
            route("/create") {
                //localhost:8080/notes/create
                post("/notes"){
                    val body = call.receive<String>()
                    call.respond(body)
                }
            }
            delete("/notes") {
                val body = call.receive<String>()
                call.respond(body)
            }
        }

    }
}

data class MySession(val count: Int = 0)

