package com.lamz.core

import com.lamz.data.TodoRepository
import com.lamz.model.PaginatedResponse
import com.lamz.model.TodoItem
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        
        get{
            call.respondText { "Selamat datang di Rest API TODO LIST" }
        }

        get("/todos") {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
            val data = TodoRepository.getAllTodo(page, size)
            val total = TodoRepository.countAll()

            call.respond(
                PaginatedResponse(
                    page = page,
                    size = size,
                    total = total,
                    data = data
                )
            )
        }

        get("/todos-by-title/{title}"){
            val title = call.parameters["title"]
            val todo = title?.let { TodoRepository.getTodoByTitleAndCompletedRaw(title) }
            if (todo != null){
                call.respond(todo)
            }else{
                call.respond(HttpStatusCode.NotFound)
            }
        }


        get("/todos/{id}") {
                val id = call.parameters["id"]
                val todo = id?.let { TodoRepository.getById(id) }
                if (todo != null){
                    call.respond(todo)
                }else{
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post("/create-todos") {
                val todo = call.receive<TodoItem>()
                val saved = TodoRepository.addTodo(todo.copy(id = UUID.randomUUID().toString()))
                call.respond(HttpStatusCode.Created, saved)
            }

            put("update-todos/{id}"){
                val id = call.parameters["id"]
                val newTodo = call.receive<TodoItem>()

                    if(id != null && TodoRepository.updateTodo(id, newTodo)){
                        call.respondText("Succesfully Update Data")
                    }else{
                        call.respond(HttpStatusCode.NotFound)
                    }
            }

        delete("delete-todos/{id}") {
            val id = call.parameters["id"]
            if (id != null && TodoRepository.delete(id)){
                call.respondText("Deleted successfully") // ✅ Ubah jadi berhasil
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }



    }
}
