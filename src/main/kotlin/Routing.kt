package com.tkjen

import com.tkjen.model.Priority
import com.tkjen.model.Task
import com.tkjen.model.User
import com.tkjen.repository.TaskRepository
import com.tkjen.repository.TaskRepository.tasksByPriority
import com.tkjen.utils.tasksAsTable
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
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



        route("/tasks") {
            get(){
                val tasks = TaskRepository.allTasks()
                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = tasks.tasksAsTable()
                )
            }
            post() {
                val formContent = call.receiveParameters()

                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )

                if (params.toList().any { it.isEmpty() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val priority = Priority.valueOf(params.third)
                    TaskRepository.addTask(
                        Task(
                            params.first,
                            params.second,
                            priority
                        )
                    )

                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/byPriority/{priority?}"){
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
            get("/byName/{taskName}") {
                val name = call.parameters["taskName"]
                if (name == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val task = TaskRepository.taskByName(name)
                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = listOf(task).tasksAsTable()
                )
            }


        }
    }
}
