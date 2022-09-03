package com.example.indigitalstudy.fragments

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.*


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
        savedInstanceState: Bundle?,
    ): View? {

        mAuth = FirebaseAuth.getInstance()
        bindingClassProf = FragmentProfileBinding.inflate(layoutInflater)
        // create object with type of GraphView
        val graph:GraphView = bindingClassProf.graph
        // fill our array of data for graph
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
            DataPoint(0.0, 1.0),
            DataPoint(1.0, 5.0),
            DataPoint(2.0, 3.0),
            DataPoint(3.0, 2.0),
            DataPoint(4.0, 6.0)
        ))
        graph.addSeries(series)

        return bindingClassProf.root


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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