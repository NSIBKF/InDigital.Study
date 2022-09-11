package com.example.indigitalstudy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.indigitalstudy.adapters.UsersAdapter
import com.example.indigitalstudy.databinding.ActivityUsersBinding
import com.example.indigitalstudy.models.User
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
        getUsers()
        setContentView(binding.root)

    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getUsers() {
        loading(true)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTIONS_USERS)
            .get()
            .addOnCompleteListener {
                loading(false)
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if(it.isSuccessful && it.result != null) {
                    val users: MutableList<User> = ArrayList()
                    for (queryDocumentSnapshot : QueryDocumentSnapshot in it.result) {
                        if (currentUserId.equals(queryDocumentSnapshot.id)) {
                            continue
                        }
                        val user = User()
                        user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME)
                        user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)
                        user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)
                        users.add(user)
                    }
                    if (users.size > 0) {
                        val usersAdapter = UsersAdapter(users)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.isVisible = true
                    } else {
                        showErrorMessage()
                    }
                } else {
                    showErrorMessage()
                }
            }
    }

    private fun showErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "No user available")
        binding.textErrorMessage.isVisible = true
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }
}