package com.lamz.data

import com.lamz.model.TodoItem
import com.lamz.model.Todos
import com.lamz.model.toTodoItem
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

import java.util.*


object TodoRepository {
    fun getAllTodo(page : Int, size : Int) : List<TodoItem> = transaction{
        Todos.selectAll()
            .limit(size)
            .offset(start = ((page - 1) * size).toLong())
            .map { it.toTodoItem() }
    }

    fun countAll() : Long = transaction {
        Todos.selectAll().count()
    }

    fun getTodoByTitleAndCompletedRaw(title: String): TodoItem? = transaction {
        Todos.selectAll()
            .where{ (Todos.title eq title) and (Todos.completed eq true) }
            .limit(1)
            .map { it.toTodoItem() }
            .singleOrNull()
    }


    fun getById(id : String) : TodoItem? = transaction {
        Todos.select (Todos.id eq UUID.fromString(id))
            .mapNotNull { it.toTodoItem() }
            .singleOrNull()
    }

    fun addTodo(todo : TodoItem): TodoItem = transaction {
        val inserted = Todos.insert {
            it[id] = UUID.fromString(todo.id)
            it[title] = todo.title
            it[completed] = todo.completed
            it[desc] = todo.desc
            it[date] = todo.date
        }

        inserted.resultedValues?.first()?.toTodoItem() ?: todo
    }

    fun updateTodo(id : String, newTodo : TodoItem): Boolean = transaction {
        Todos.update({ Todos.id eq UUID.fromString(id) }) {
            it[title] = newTodo.title
            it[completed] = newTodo.completed
            it[desc] = newTodo.desc
            it[date] = newTodo.date
        } > 0
    }

    fun delete(id: String): Boolean = transaction {
        Todos.deleteWhere { Todos.id eq UUID.fromString(id) } > 0
    }
}