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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.indigitalstudy.R
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var videoBG: VideoView
    private lateinit var preferencesManager: PreferenceManager
    lateinit var sharedPreferences: SharedPreferences
    private var mMediaPlayer: MediaPlayer? = null
    private var mCurrentVideoPosition: Int = 0
    private val PREFS_NAME: String = "PrefsFile"
    private var isRemembered = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        preferencesManager = PreferenceManager(applicationContext)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isRemembered = sharedPreferences.getBoolean("CHECK_BOX", false)

        val SignUpText = findViewById<TextView>(R.id.sign_up_text)
        SignUpText.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        if (isRemembered) {
            val i = Intent(this, MainActivity::class.java)
            startActivityForResult(i, 100)
            finish()
        }
        Log.d("tag", "onCreateLA")
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





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == resultCode && data == null)
        {
            //если пользователь заходил несколько минут назад, то передадим данные (логин, пароль)
            //чтобы не приходилось вводить заново
            //Хотя есть вариант, что логин и пароль не будут стираться после входа в приложение вообще
            //Но из-за этого логин и пароль будут выводиться всегда не зависимо от прошедшего времени
            //Из-за этого любой человек, который возьмет телефон пользователя сможет увидеть логин и пароль только нажатием кнопки назад
            //А пока это просто карказ
        }
        else if (resultCode == 101 && data == null) {
            findViewById<EditText>(R.id.email_input).text = null
            findViewById<EditText>(R.id.password_input).text = null
        }
    }

    @SuppressLint("CutPasteId")
    override fun onClick(view: View) {
        val remember = findViewById<CheckBox>(R.id.remember_me)
        val checked: Boolean = remember.isChecked
        val email = findViewById<EditText>(R.id.email_input).text.toString()
        val password = findViewById<EditText>(R.id.password_input).text.toString()



         if(validate(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    val edit: SharedPreferences.Editor = sharedPreferences.edit()
                    edit.putBoolean("CHECK_BOX", checked)
                    edit.apply()
                    val i = Intent(this, MainActivity::class.java)
                    startActivityForResult(i, 100)
                    finish()
                } else if (validate(email, password)) {
                    val database : FirebaseFirestore = FirebaseFirestore.getInstance()
                    database.collection(Constants.KEY_COLLECTIONS_USERS)
                        .whereEqualTo(Constants.KEY_EMAIL, email)
                        .whereEqualTo(Constants.KEY_PASSWORD, password)
                        .get()
                        .addOnCompleteListener {
                            if(it.isSuccessful && it.result != null && it.result.documents.size > 0) {
                                val documentSnapshot : DocumentSnapshot = it.result.documents[0]
                                preferencesManager.putBoolean(Constants.KEY_IS_SIGN_IN, true)
                                preferencesManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                                preferencesManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME))
                                preferencesManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE))
                                val intent : Intent = Intent(applicationContext, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK / Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                            }
                        }
                }
                else {
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
