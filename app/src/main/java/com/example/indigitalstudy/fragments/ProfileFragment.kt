package com.example.indigitalstudy.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.indigitalstudy.LoginActivity
import com.example.indigitalstudy.MainActivity
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    lateinit var bindingClassProf : FragmentProfileBinding
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    private val PREFS_NAME: String = "PrefsFile"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mAuth = FirebaseAuth.getInstance()
        bindingClassProf = FragmentProfileBinding.inflate(layoutInflater)

        return bindingClassProf.root


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        bindingClassProf.OutBtn.setOnClickListener {
            sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val edit: SharedPreferences.Editor = sharedPreferences.edit()
            edit.clear()
            edit.apply()
            Log.d("tag", "файл изменился")

            mAuth.signOut()

            activity?.setResult(101, null)
            activity?.finish()

       }


    }
    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}