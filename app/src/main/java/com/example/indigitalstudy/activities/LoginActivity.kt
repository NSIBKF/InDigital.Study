package com.example.indigitalstudy.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.indigitalstudy.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val PREFS_NAME: String = "PrefsFile"
    private val isLogOutBtnPressedInPFCode: Int = 101

    private lateinit var mAuth: FirebaseAuth
    private lateinit var videoBG: VideoView
    lateinit var sharedPreferences: SharedPreferences
    private var mMediaPlayer: MediaPlayer? = null
    private var mCurrentVideoPosition: Int = 0
    private var isRemembered: Boolean = false
    private var mainLauncher: ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isRemembered = sharedPreferences.getBoolean("CHECK_BOX", false)
        mainLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == isLogOutBtnPressedInPFCode) {
                findViewById<EditText>(R.id.email_input).text = null
                findViewById<EditText>(R.id.password_input).text = null
                //example "how can we get a data from another activity?"
                //val text = result.data?.getStringExtra("key1")
            }
        }
        /* actions if user click on sign up button */
        val signUpText = findViewById<TextView>(R.id.sign_up_text)
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        /* check "did user set option remember me?" */
        if (isRemembered) {
            //val i = Intent(this, MainActivity::class.java)
            //startActivityForResult(i, isBackPressedInMACode)
            mainLauncher?.launch(Intent(this, MainActivity::class.java))
            finish()
        }
        Log.d("AL_M", "onCreateLA_M")
        /* loading background video  */
        videoBG = findViewById<VideoView>(R.id.videoView)
        val uri = Uri.parse("android.resource://"
                + packageName
                + "/"
                + R.raw.background_login_activity
        )
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
        val loginBtn = findViewById<Button>(R.id.login_btn)
        loginBtn.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
    }

    @SuppressLint("CutPasteId")
    override fun onClick(view: View) {
        /* All actions when we try to log in */
        val rememberMe = findViewById<CheckBox>(R.id.remember_me)
        val checked: Boolean = rememberMe.isChecked
        val email = findViewById<EditText>(R.id.email_input).text.toString()
        val password = findViewById<EditText>(R.id.password_input).text.toString()
        if(validate(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    val edit: SharedPreferences.Editor = sharedPreferences.edit()
                    edit.putBoolean("CHECK_BOX", checked)
                    edit.apply()
                    //val i = Intent(this, MainActivity::class.java)
                    //startActivityForResult(i, isBackPressedInMACode)
                    mainLauncher?.launch(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect email or password. Try again", Toast.LENGTH_SHORT)
                        .show()
                    findViewById<EditText>(R.id.password_input).text = null
                }
            }
        }
        else {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validate(email: String, password:String) =
        email.isNotEmpty() && password.isNotEmpty()

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
        //setResult(100, null)
    }
}
