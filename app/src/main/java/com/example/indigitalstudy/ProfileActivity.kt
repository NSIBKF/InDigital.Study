package com.example.indigitalstudy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.indigitalstudy.databinding.ActivityMainBinding
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity: AppCompatActivity() {
    lateinit var bindingClassProf : FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClassProf = FragmentProfileBinding.inflate(layoutInflater)
        setContentView(bindingClassProf.root)

        mAuth = FirebaseAuth.getInstance()

        bindingClassProf.OutBtn.setOnClickListener {
            bindingClassProf.textView.text = "Changed"
                    mAuth.signOut()
        }



        mAuth.addAuthStateListener {
           if (it.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser == null)
        {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

}