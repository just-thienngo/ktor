package com.tkjen

import com.tkjen.model.Priority
import com.tkjen.model.User
import com.tkjen.repository.TaskRepository
import com.tkjen.repository.TaskRepository.tasksByPriority
import com.tkjen.utils.tasksAsTable
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {

        staticResources("/task-ui", "task-ui")

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
            val tasks = TaskRepository.allTasks()
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = tasks.tasksAsTable()
            )
        }

        get("/tasks/byPriority/{priority?}"){
            val priorityAsText = call.parameters["priority"] // Lấy giá trị priority từ URL vi du High Low
            if(priorityAsText == null)
            {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            try {
                val priority = Priority.valueOf(priorityAsText)
                val tasks = TaskRepository.tasksByPriority(priority)

                if (tasks.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respondText(tasks.tasksAsTable(), ContentType.Text.Html)

            }catch(ex: IllegalArgumentException) {
                log.warn("Invalid priority: $priorityAsText", ex)
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
