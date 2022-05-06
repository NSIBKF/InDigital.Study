package com.example.indigitalstudy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    lateinit var bindingClassProf : FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth

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
            
        }


    }
    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}