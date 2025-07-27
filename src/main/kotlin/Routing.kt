package com.tkjen

import com.tkjen.model.User
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/user") {
            val user = User(1, "Ngô Chí Thiện")
            call.respond(user)
        }
        post("/user") {
            val user = call.receive<User>()
            call.respondText("Received user: ${user.name}")
        }
        get("/tasks"){
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = """
                <h3>TODO:</h3>
                <ol>
                    <li>A table of all the tasks</li>
                    <li>A form to submit new tasks</li>
                </ol>
                """.trimIndent()
            )
        }
    }
}
