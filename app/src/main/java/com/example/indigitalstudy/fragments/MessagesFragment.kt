package com.example.indigitalstudy.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.indigitalstudy.activities.ChatActivity
import com.example.indigitalstudy.activities.UsersActivity
import com.example.indigitalstudy.adapters.RecentConversationsAdapter
import com.example.indigitalstudy.databinding.FragmentMessagesBinding
import com.example.indigitalstudy.listeners.ConversionListener
import com.example.indigitalstudy.models.ChatMessage
import com.example.indigitalstudy.models.User
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


/**
 * A simple [Fragment] subclass.
 */
class MessagesFragment : Fragment(), ConversionListener {

    private lateinit var binding : FragmentMessagesBinding
    private lateinit var preferenceManager : PreferenceManager
    private lateinit var conversations : MutableList<ChatMessage>
    private lateinit var conversationsAdapter : RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMessagesBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(context)
        loadUserDetails()
        init()
        setListeners()
        listenConversations()
        return binding.root

    }

    private fun init() {
        conversations = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversations, this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes : ByteArray? = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap : Bitmap? = bytes?.let { BitmapFactory.decodeByteArray(bytes, 0, it.size) }
        binding.ImageProfile.setImageBitmap(bitmap)
    }

    private fun setListeners() {
        binding.BtnNewChat.setOnClickListener {
            startActivity(Intent(context, UsersActivity::class.java))
        }
    }

    private fun listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        return@EventListener
                    }
                    if (value != null) {
                        for (documentChange in value.documentChanges) {
                            if (documentChange.type == DocumentChange.Type.ADDED) {
                                val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                                val receiverId =
                                    documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                                val chatMessage = ChatMessage()
                                chatMessage.senderId = senderId
                                chatMessage.receiverId = receiverId
                                if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                                    chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                                    chatMessage.conversionName = documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                                    chatMessage.conversionId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                                } else {
                                    chatMessage.conversionImage = documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                                    chatMessage.conversionName = documentChange.document.getString(Constants.KEY_SENDER_NAME)
                                    chatMessage.conversionId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                                }
                                chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                conversations.add(chatMessage)
                            } else if(documentChange.type == DocumentChange.Type.MODIFIED) {
                                for (i in conversations.indices) {
                                    val senderId : String? = documentChange.document.getString(Constants.KEY_SENDER_ID)
                                    val receiverId: String? = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                                    if(conversations[i].senderId.equals(senderId) && conversations[i].receiverId.equals(receiverId)) {
                                        conversations[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                        conversations[i].dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                        break
                                    }
                                }
                            }
                        }
                        conversations.sortWith(compareByDescending { it.dateObject })
                        conversationsAdapter.notifyDataSetChanged()
                        binding.conversationsRecyclerView.smoothScrollToPosition(0)
                        binding.conversationsRecyclerView.isVisible = true
                        binding.progressBar.isVisible = false
                    }
                }

    override fun onConversionClicked(user: User?) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }
}