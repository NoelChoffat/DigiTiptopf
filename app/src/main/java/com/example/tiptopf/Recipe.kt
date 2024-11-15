package com.example.tiptopf

import java.util.UUID

data class Recipe(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val ingredients: String,
    val preparation: String
)