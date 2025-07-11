package com.lamz.core

import com.lamz.data.TodoRepository
import com.lamz.model.PaginatedResponse
import com.lamz.model.TodoItem
import core.ApiResponse
import core.ErrorResponse
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

            if (size > 0){
                call.respond(
                    status = HttpStatusCode.OK,
                    ApiResponse(
                        status = 200,
                        message = "Successfuly Get Data Todo",
                        data = PaginatedResponse(
                            page = page,
                            size = size,
                            total = total,
                            data = data
                        )
                    )
                )
            }else {
                call.respond(
                    status = HttpStatusCode.OK,
                    ApiResponse(
                        status = 201,
                        message = "Data Todo Is Empty",
                        data = PaginatedResponse(
                            page = page,
                            size = size,
                            total = total,
                            data = data
                        )
                    )
                )
            }

        }

        get("/todos-by-title/{title}"){
            val title = call.parameters["title"]
            val todo = title?.let { TodoRepository.getTodoByTitleAndCompletedRaw(title) }
            if (todo != null){
                call.respond(todo)
            }else{
                call.respond(status = HttpStatusCode.NotFound,
                    ErrorResponse(
                        status = 404,
                        message = "Todo with title $title and completed=true not found"
                    ))
            }
        }


        get("/todos/{id}") {
                val id = call.parameters["id"]
                val todo = id?.let { TodoRepository.getById(id) }
                if (todo != null){
                    call.respond(todo)
                }else{
                    call.respond(status = HttpStatusCode.NotFound,
                        ErrorResponse(
                            status = 404,
                            message = "Data todo not found"
                        ))
                }
            }

            post("/create-todos") {
                val todo = call.receive<TodoItem>()
                val saved = TodoRepository.addTodo(todo.copy(id = UUID.randomUUID().toString()))
                call.respond(HttpStatusCode.Created, ApiResponse(
                    status = 200,
                    message = "Success add data todo",
                    data = saved
                ))
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
                call.respondText("Deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }



    }
}
