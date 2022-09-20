package com.example.indigitalstudy.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.indigitalstudy.R
import com.example.indigitalstudy.databinding.ActivityMainBinding
import com.example.indigitalstudy.fragments.*
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val searchFragment = SearchFragment()
    private val homeFragment = MainFragment()
    private val personFragment = ProfileFragment()
    private val scheduleFragment = ScheduleFragment()
    private val messagesFragment = MessagesFragment()

    private lateinit var bindingClass: ActivityMainBinding
    lateinit var preferenceManager: PreferenceManager
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

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        /* устанавливаем возвращаемый result code, сообщающий о том,
        что выход осуществлен через log out
        isBackPressed принимает значение true только при нажатии Back
        А при нажатии все данные, хранившиеся в переменных стираются,
        следовательно isBackPressed примет значение по умолчанию false
        По этой причине, если пользователь нажмет только log out, то
        произойдет переход на LoginActivity */
        if (!isBackPressed) {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
        /*
        //Удаление токена в случае выхода без нажатия log out
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference =
            database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
            )
        val updates: HashMap<String, Any> = HashMap()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(updates)
            .addOnSuccessListener {
                preferenceManager.clear()
                showToast("Token deleted")
            }

         */

    }

}


