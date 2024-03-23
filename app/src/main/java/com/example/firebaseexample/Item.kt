package com.example.firebaseexample

// Define a data class Item to represent an item in the database
data class Item(
    val id: String = "",     // Unique identifier for the item
    val title: String = "",  // Title of the item
    val name: String = "",   // Name or title of the item
    val email: String = ""   // Email associated with the item
)
