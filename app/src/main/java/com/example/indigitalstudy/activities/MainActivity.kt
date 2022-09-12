package com.example.indigitalstudy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.indigitalstudy.R
import com.example.indigitalstudy.databinding.ActivityMainBinding
import com.example.indigitalstudy.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val searchFragment = SearchFragment()
    private val homeFragment = MainFragment()
    private val personFragment = ProfileFragment()
    private val scheduleFragment = ScheduleFragment()
    private val messagesFragment = MessagesFragment()

    private lateinit var bindingClass: ActivityMainBinding
    private var isBackPressed: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        replaceFragment(homeFragment)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
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
        //устанавливаем возвращаемый result code, сообщающий о том,
        //что выход осуществлен через log out
        //isBackPressed принимает значение true только при нажатии Back
        //А при нажатии все данные, хранившиеся в переменных стираются,
        //следовательно isBackPressed примет значение по умолчанию false
        //По этой причине, если пользователь нажмет только log out, то
        //произойдет переход на LoginActivity
        //setResult(isBackPressedInMACode, null)
        if (!isBackPressed) {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

    }

}


