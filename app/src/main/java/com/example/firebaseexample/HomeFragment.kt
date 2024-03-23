package com.example.firebaseexample

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    // Initialize variables
    private var currentDrawerTitle: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var itemEditText: EditText
    private lateinit var addButton: Button
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("items")
    private lateinit var currentUserEmail: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

        // Retrieve the title of the first item in the navigation drawer menu
        val firstMenuItem = navigationView.menu.findItem(R.id.nav_item_1)
        currentDrawerTitle = firstMenuItem.title.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navigationView = view.findViewById(R.id.nav_view)
        itemEditText = view.findViewById(R.id.editText)
        addButton = view.findViewById(R.id.addButton)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Retrieve the title of the first item in the navigation drawer menu
        val firstMenuItem = navigationView.menu.findItem(R.id.nav_item_1)
        currentDrawerTitle = firstMenuItem.title.toString()

        // Get the current user's email
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        // Set up RecyclerView and populate data from Firebase database
        setupRecyclerView()

        // Set up navigation drawer
        setupNavigationDrawer()

        // Set onClickListener for the logout button in the navigation drawer header
        navigationView.getHeaderView(0).findViewById<View>(R.id.logoutButton).setOnClickListener {
            logoutUser()
        }

        // Set up button to open the navigation drawer
        view.findViewById<Button>(R.id.openDrawerButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up button to add item to the list
        addButton.setOnClickListener {
            val itemText = itemEditText.text.toString().trim()
            if (itemText.isNotEmpty()) {
                addItem(itemText)
                itemEditText.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please enter an item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        // Set up Firebase RecyclerView adapter with query
        val query = databaseReference
            .orderByChild("title")
            .equalTo("${currentDrawerTitle}_${currentUserEmail}")

        val firebaseRecyclerOptions = FirebaseRecyclerOptions.Builder<Item>()
            .setQuery(query, Item::class.java)
            .build()

        val adapter = ItemAdapter(firebaseRecyclerOptions, databaseReference)
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun logoutUser() {
        // Log out the current user and redirect to LoginActivity
        FirebaseAuth.getInstance().signOut()
        activity?.let {
            it.startActivity(Intent(it, LoginFirebase::class.java))
            it.finish()
        }
    }

    private fun setupNavigationDrawer() {
        // Set up ActionBarDrawerToggle
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            activity as AppCompatActivity,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up navigation item selected listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Close the drawer when an item is clicked
            drawerLayout.closeDrawer(GravityCompat.START)

            // Handle navigation item clicks here
            val newTitle = menuItem.title.toString()
            if (newTitle != currentDrawerTitle) {
                currentDrawerTitle = newTitle
                updateRecyclerView()
            }

            true
        }

        // Display user email in navigation drawer header
        val user = FirebaseAuth.getInstance().currentUser
        val userEmailTextView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.userEmailTextView)
        userEmailTextView.text = user?.email ?: ""
    }

    private fun updateRecyclerView() {
        // Update Firebase query with filter by composite key
        val query = databaseReference
            .orderByChild("title")
            .equalTo("${currentDrawerTitle}_${currentUserEmail}")

        val firebaseRecyclerOptions = FirebaseRecyclerOptions.Builder<Item>()
            .setQuery(query, Item::class.java)
            .build()

        val adapter = ItemAdapter(firebaseRecyclerOptions, databaseReference)
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun addItem(itemText: String) {
        // Add item to the Firebase database
        val user = FirebaseAuth.getInstance().currentUser
        val newItemRef = databaseReference.push()
        val compositeKey = "${currentDrawerTitle}_${user?.email}"
        val item = Item(newItemRef.key!!, compositeKey, itemText.toString(), user?.email ?: "")
        newItemRef.setValue(item)
            .addOnSuccessListener {
                // Notify the adapter that the data set has changed
                recyclerView.adapter?.notifyDataSetChanged()
                // Display a toast message to inform the user
                Toast.makeText(requireContext(), "Item added to the list", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the operation
                Toast.makeText(requireContext(), "Failed to add item: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeItem(item: Item) {
        // Remove item from the Firebase database
        val itemRef = databaseReference.child(item.id)
        itemRef.removeValue()
        // Notify the adapter that the data set has changed
        recyclerView.adapter?.notifyDataSetChanged()
    }
}
