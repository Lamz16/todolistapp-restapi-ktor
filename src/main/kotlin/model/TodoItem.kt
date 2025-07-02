package com.lamz.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

@Serializable
data class TodoItem(
    val id : String = UUID.randomUUID().toString(),
    val title : String,
    val completed : Boolean = false,
    val desc : String,
    val date : String
)

object Todos : UUIDTable("todos") {
    val title = varchar("title", 255)
    val completed = bool("completed").default(false)
    val desc = text("desc")
    val date = varchar("date", 100)
}

fun ResultRow.toTodoItem() = TodoItem(
    id = this[Todos.id].value.toString(),
    title = this[Todos.title],
    completed = this[Todos.completed],
    desc = this[Todos.desc],
    date = this[Todos.date]
)

@Serializable
data class PaginatedResponse<T>(
    val page: Int,
    val size: Int,
    val total: Long,
    val data: List<T>
)