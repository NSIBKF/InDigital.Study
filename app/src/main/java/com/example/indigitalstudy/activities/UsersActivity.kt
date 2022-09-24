package com.example.indigitalstudy.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.indigitalstudy.R
import com.example.indigitalstudy.adapters.UsersAdapter
import com.example.indigitalstudy.databinding.ActivityUsersBinding
import com.example.indigitalstudy.listeners.UserListener
import com.example.indigitalstudy.models.User
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UsersActivity : AppCompatActivity(), UserListener {
    private lateinit var binding: ActivityUsersBinding
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
        Log.d("tag_UA", "Start method: getUsers()")
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
                if (it.isSuccessful && it.result != null) {
                    val users: MutableList<User> = ArrayList()
                    for (queryDocumentSnapshot: QueryDocumentSnapshot in it.result) {
                        if (currentUserId.equals(queryDocumentSnapshot.id)) {
                            continue
                        }
                        val user = User()
                        user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME)
                        user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)
                        if (queryDocumentSnapshot.getString(Constants.KEY_IMAGE) != null) {
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)
                            Log.d("tag_UA", "User: ${user.name}")
                        }
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)
                        user.id = queryDocumentSnapshot.id
                        users.add(user)
                    }
                    if (users.size > 0) {
                        val usersAdapter = UsersAdapter(users, this)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.isVisible = true
                        Log.d("tag_UA", "User: $currentUserId")
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

    override fun onUserClicked(user: User?) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        finish()
    }
}