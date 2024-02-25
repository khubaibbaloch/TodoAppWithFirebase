package com.example.todolist

import java.util.Date

data class User(
    val taskId: String?= null,
    val tittle:String="",
    val detail:String="",
    val completed:Boolean=false,
    val color: Int = 0
)

