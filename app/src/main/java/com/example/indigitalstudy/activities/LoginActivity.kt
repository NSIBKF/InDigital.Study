package com.example.indigitalstudy.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.indigitalstudy.R
import com.example.indigitalstudy.databinding.ActivityLoginBinding
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val fPrefName: String = "PrefsFile"
    private val isLogOutBtnPressedInPFCode: Int = 101

    private lateinit var bindingClass: ActivityLoginBinding
    private lateinit var videoBG: VideoView
    private lateinit var preferencesManager: PreferenceManager
    private lateinit var sharedPreferences: SharedPreferences
    private var mMediaPlayer: MediaPlayer? = null
    private var mCurrentVideoPosition: Int = 0
    private var isRemembered: Boolean = false
    private var mainLauncher: ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        preferencesManager = PreferenceManager(applicationContext)
        /* getting a value from file contains a key for isRemember */
        sharedPreferences = getSharedPreferences(fPrefName, Context.MODE_PRIVATE)
        isRemembered = sharedPreferences.getBoolean("CHECK_BOX", false)
        /* conditions for any ways of using result codes from MainActivity */
        mainLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == isLogOutBtnPressedInPFCode) {
                bindingClass.emailInput.text = null
                bindingClass.passwordInput.text = null
                //example "how can we get a data from another activity?"
                //val text = result.data?.getStringExtra("key1")
            }
        }
        /* check "did user set option remember me?" */
        if (isRemembered) {
            mainLauncher?.launch(Intent(this, MainActivity::class.java))
            finish()
        }
        /* loading background video  */
        videoBG = bindingClass.videoView
        val uri = Uri.parse("android.resource://"
                + packageName
                + "/"
                + R.raw.background_login_activity
        )
        loadBGVideo(uri)
        /* actions if user will click on sign up button */
        val signUpText = bindingClass.signUpText
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        /* Actions if user will click on login button */
        val loginBtn = bindingClass.loginBtn
        loginBtn.setOnClickListener(this)
    }

    private fun loadBGVideo(uri: Uri?) {
        videoBG.setVideoURI(uri)
        videoBG.start()
        videoBG.setOnPreparedListener {
            mMediaPlayer = it
            mMediaPlayer!!.isLooping = true
            if (mCurrentVideoPosition != 0) {
                mMediaPlayer!!.seekTo(mCurrentVideoPosition)
                mMediaPlayer!!.start()
            }
        }
    }

    @SuppressLint("CutPasteId")
    override fun onClick(view: View) {
        /* All actions when we try to log in */
        val rememberMe = bindingClass.rememberMe
        val checked: Boolean = rememberMe.isChecked
        val email = bindingClass.emailInput.text.toString()
        val password = bindingClass.passwordInput.text.toString()

         if (validate(email, password)) {
                    val database: FirebaseFirestore = FirebaseFirestore.getInstance()
                    database.collection(Constants.KEY_COLLECTIONS_USERS)
                        .whereEqualTo(Constants.KEY_EMAIL, email)
                        .whereEqualTo(Constants.KEY_PASSWORD, password)
                        .get()
                        .addOnCompleteListener {
                            if (it.isSuccessful && it.result != null && it.result.documents.size > 0) {
                                //заносим ключ в файл, чтобы сохранить данные о запоминании входа
                                val edit: SharedPreferences.Editor = sharedPreferences.edit()
                                edit.putBoolean("CHECK_BOX", checked)
                                edit.apply()
                                val documentSnapshot: DocumentSnapshot = it.result.documents[0]
                                preferencesManager.putBoolean(Constants.KEY_IS_SIGN_IN, true)
                                preferencesManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                                preferencesManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME))
                                preferencesManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE))
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK / Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                mainLauncher?.launch(intent)
                                finish()
                            } else {
                                showToast("Incorrect email or password. Try again")
                                bindingClass.passwordInput.text = null
                            }
                        }
         } else {
             showToast("Please enter email and password")
         }

    }

    private fun validate(email: String, password:String) =
        email.isNotEmpty() && password.isNotEmpty()

    private fun showToast(message: String?) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        mCurrentVideoPosition = mMediaPlayer!!.currentPosition
        videoBG.pause()
    }

    override fun onResume() {
        super.onResume()
        videoBG.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isRemembered) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

}
