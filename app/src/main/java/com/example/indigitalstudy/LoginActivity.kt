package com.example.indigitalstudy

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("tag", "onCreateLA")

        val login_btn = findViewById<Button>(R.id.login_btn)
        login_btn.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onBackPressed() {
        super.onBackPressed()
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

    override fun onClick(view: View) {
        val email = findViewById<EditText>(R.id.email_input).text.toString()
        val password = findViewById<EditText>(R.id.password_input).text.toString()
        if(validate(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    val i = Intent(this, MainActivity::class.java)
                    //startActivity(Intent(this, MainActivity::class.java))
                    startActivityForResult(i, 100)
                }
                else {
                    // всплывающие мини иконки об ошибках
                    findViewById<EditText>(R.id.email_input).setError("Mistake")
                    findViewById<EditText>(R.id.password_input).setError("Mistake2")
                    //findViewById<EditText>(R.id.email_input).requestFocus()
                    //findViewById<EditText>(R.id.password_input).requestFocus()

                    Toast.makeText(this, "Incorrect email or password. Try again", Toast.LENGTH_SHORT)
                        .show()
                    findViewById<EditText>(R.id.email_input).text = null
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


}