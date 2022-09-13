package com.example.indigitalstudy.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.indigitalstudy.adapters.ChatAdapter
import com.example.indigitalstudy.databinding.ActivityChatBinding
import com.example.indigitalstudy.models.ChatMessage
import com.example.indigitalstudy.models.User
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser : User
    private lateinit var chatMessages : MutableList<ChatMessage>
    private lateinit var chatAdapter : ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore
    private var conversionId : String? = null
    private var isReceiverAvailable : Boolean = false




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadReceiverDetails()
        init()
        listenMessages()
    }

    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            chatMessages,
            getBitmapFromEncodedStrings(receiverUser.image),
            preferenceManager.getString(Constants.KEY_USER_ID)
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun sendMessage() {
        val message : HashMap<String, Any> = HashMap()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        if(conversionId != null) {
            updateConversion(binding.inputMessage.text.toString())
        } else {
            val conversion : HashMap<String, Any> = HashMap()
            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)
            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME)
            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE)
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image
            conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        binding.inputMessage.text = null
    }

    private fun listenAvailabilityOfReceiver() {
        database.collection(Constants.KEY_COLLECTIONS_USERS).document(
            receiverUser.id
        ).addSnapshotListener{value, error ->
            if(error != null) {
                return@addSnapshotListener
            }
            if(value != null) {
                if(value.getLong(Constants.KEY_AVAILABILITY) != null) {
                    val availability : Any = Objects.requireNonNull(
                        value.getLong(Constants.KEY_AVAILABILITY)
                    ).toString().toInt()
                    isReceiverAvailable = availability == 1
                }
            }
            binding.textAvailability.isVisible = isReceiverAvailable
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }




    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if(error != null) {
                return@EventListener
            }
            if(value != null) {
                val count : Int = chatMessages.size
                for(documentChange: DocumentChange in value.documentChanges) {
                    if(documentChange.type == DocumentChange.Type.ADDED) {
                        val chatMessage : ChatMessage = ChatMessage()
                        chatMessage.senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        chatMessage.receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        chatMessage.message = documentChange.document.getString(Constants.KEY_MESSAGE)
                        chatMessage.dateTime =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                ?.let { getReadableDateTime(it) }
                        chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        chatMessages.add(chatMessage)
                    }
                }
                chatMessages.sortWith(compareBy { it.dateObject })
                if(count == 0) {
                    chatAdapter.notifyDataSetChanged()
                } else {
                    chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                    binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
                }
                binding.chatRecyclerView.isVisible = true
            }
            binding.progressBar.isVisible = false
            if(conversionId == null) {
                checkForConversion()
            }
        }

    private fun getBitmapFromEncodedStrings(encodedImage: String) : Bitmap {
        val bytes:ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    private fun addConversion(conversion : HashMap<String, Any>) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener {
                conversionId = it.id
            }
    }

    private fun updateConversion(message: String) {
        val documentReference : DocumentReference? =
            conversionId?.let {
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(
                    it
                )
            }
        documentReference?.update(
            Constants.KEY_LAST_MESSAGE, message,
            Constants.KEY_TIMESTAMP, Date()
        )
    }

    private fun checkForConversion() {
        if(chatMessages.size != 0) {
            checkForConversionRemotely(
                preferenceManager.getString(Constants.KEY_USER_ID),
                receiverUser.id
            )
            checkForConversionRemotely(
                receiverUser.id,
                preferenceManager.getString(Constants.KEY_USER_ID)
            )
        }
    }

    private fun checkForConversionRemotely(senderId: String, receiverId : String) {
            database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener)
    }

    private val conversionOnCompleteListener = OnCompleteListener { task: Task<QuerySnapshot?> ->
        if (task.isSuccessful && task.result != null && task.result!!
                .documents.size > 0
        ) {
            val documentSnapshot = task.result!!.documents[0]
            conversionId = documentSnapshot.id
        }
    }

    override fun onResume() {
        super.onResume()
        listenAvailabilityOfReceiver()
    }
}