package com.example.indigitalstudy.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.indigitalstudy.LoginActivity
import com.example.indigitalstudy.MainActivity
import com.example.indigitalstudy.ProfileActivity
import com.example.indigitalstudy.ViewModel
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    lateinit var bindingClassProf : FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private val dataModel: ViewModel by activityViewModels()



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
            bindingClassProf.textView.text = "Changed"
            mAuth.signOut()
            dataModel.boolParametr.value = true

        }


    }
    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}