package com.wahrani.amine.model

data class Channel(
    val name: String,
    val url: String,
    val logoUrl: String = "",
    val groupTitle: String = "Uncategorized",
    val tvgId: String = "",
    val sourceLabel: String = ""
)
