package com.example.indigitalstudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.indigitalstudy.databinding.ActivityMainBinding
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.example.indigitalstudy.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var bindingClass :ActivityMainBinding

    private val searchFragment = SearchFragment()
    private val homeFragment = MainFragment()
    private val settingsFragment = SettingsFragment()
    private val personFragment = ProfileFragment()
    private val scheduleFragment = ScheduleFragment()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)



        mAuth = FirebaseAuth.getInstance()

         //  mAuth.signOut()//////////////////////////////////////////////////////////////надо поместить в кнопку!!!!!

        //   auth.signInWithEmailAndPassword("fomin.kirill02@mail.ru", "ghjnjnbg02")
        //      .addOnCompleteListener {
        //          if(it.isSuccessful) {
//
        //         }else{

        //        }
        //    }


        replaceFragment(homeFragment)


        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_main -> replaceFragment(homeFragment)
                R.id.ic_person -> replaceFragment(personFragment)
                R.id.ic_schedule -> replaceFragment(scheduleFragment)
                R.id.ic_settings -> replaceFragment(settingsFragment)
                R.id.ic_search -> replaceFragment(searchFragment)
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            //startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        //intent.getBooleanExtra("key", true)
        //startActivity(Intent(this, LoginActivity::class.java))
        setResult(100, null)
        finish()

    }

}


