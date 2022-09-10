package com.example.indigitalstudy.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.indigitalstudy.R
import com.example.indigitalstudy.databinding.ActivityMainBinding
import com.example.indigitalstudy.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    lateinit var bindingClass :ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences

    private val searchFragment = SearchFragment()
    private val homeFragment = MainFragment()
    private val personFragment = ProfileFragment()
    private val scheduleFragment = ScheduleFragment()
    private val messagesFragment = MessagesFragment()
    private val PREFS_NAME: String = "PrefsFile"
    private var isBackPressed: Boolean = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


        mAuth = FirebaseAuth.getInstance()


        replaceFragment(homeFragment)


        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_main -> replaceFragment(homeFragment)
                R.id.ic_person -> replaceFragment(personFragment)
                R.id.ic_schedule -> replaceFragment(scheduleFragment)
                R.id.ic_messages -> replaceFragment(messagesFragment)
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

    override fun onBackPressed() {
        AlertDialog.Builder(this).apply {
            setTitle("Подтверждение")
            setMessage("Вы уверены, что хотите выйти из программы?")

            setPositiveButton("Да") { _, _ ->
                isBackPressed = true
                super.onBackPressed()

            }

            setNegativeButton("Нет"){_, _ ->
                // if user press no, then return the activity
                //Toast.makeText(this@MainActivity, "Thank you",
                //    Toast.LENGTH_LONG).show()
            }
            setCancelable(true)
        }.create().show()
    }
    override fun onDestroy() {
        super.onDestroy()
        //intent.getBooleanExtra("key", true)
        //startActivity(Intent(this, LoginActivity::class.java))
        setResult(100, null)
        if (!isBackPressed) {
            val edit: SharedPreferences.Editor = sharedPreferences.edit()
            edit.clear()
            Log.d("tag", "файл изменился в MainActivity")
            edit.apply()
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

    }

}


