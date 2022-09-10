package com.example.indigitalstudy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.indigitalstudy.R
import com.example.indigitalstudy.databinding.FragmentHomeBinding
import com.example.indigitalstudy.databinding.FragmentMessagesBinding
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    private lateinit var bindingM : FragmentMessagesBinding
    private lateinit var binding: FragmentHomeBinding
    private lateinit var preferenceManager : PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingM = FragmentMessagesBinding.inflate(layoutInflater)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(context)

        getToken()
        return binding.root
    }

    private fun showToast(message:String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }

    private fun updateToken(token: String) {
        val database : FirebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference : DocumentReference =
            database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
            )
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener {
                showToast("Unable to update token")
            }
    }

}