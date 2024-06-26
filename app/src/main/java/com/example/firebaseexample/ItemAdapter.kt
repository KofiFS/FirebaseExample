package com.example.firebaseexample

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference

class ItemAdapter(
    options: FirebaseRecyclerOptions<Item>, // Options for FirebaseRecyclerAdapter
    private val databaseReference: DatabaseReference, // Reference to Firebase Database
) : FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {

    // Create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate item layout and return ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Item) {
        // Call bind method of ViewHolder to bind data
        holder.bind(model, databaseReference)
    }
}
