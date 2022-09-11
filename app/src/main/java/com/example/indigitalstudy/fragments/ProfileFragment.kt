package com.example.indigitalstudy.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.*


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private val fPrefName: String = "PrefsFile"
    private val isLogOutBtnPressedInPFCode: Int = 101

    lateinit var bindingClassProf : FragmentProfileBinding
    private lateinit var preferenceManager : PreferenceManager
    lateinit var sharedPreferences: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        bindingClassProf = FragmentProfileBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(context)
        //create object with type of GraphView
        val graph:GraphView = bindingClassProf.graph
        //fill our array of data for graph
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(arrayOf(
            DataPoint(0.0, 1.0),
            DataPoint(1.0, 5.0),
            DataPoint(2.0, 3.0),
            DataPoint(3.0, 2.0),
            DataPoint(4.0, 6.0)
        ))
        graph.addSeries(series)
        loadUserDetails()

        return bindingClassProf.root


    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bindingClassProf.OutBtn.setOnClickListener {
            //Очистка файла с ключем о том, что пользователь был запомнен
            clearFile(fPrefName)
            //Удаление токена и выход
            val database: FirebaseFirestore = FirebaseFirestore.getInstance()
            val documentReference : DocumentReference = database.collection(Constants.KEY_COLLECTIONS_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
            )
            val updates:HashMap<String, Any> = HashMap()
            updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
            documentReference.update(updates)
                .addOnSuccessListener {
                    preferenceManager.clear()
                    showToast("Token deleted")
                    activity?.setResult(isLogOutBtnPressedInPFCode, null)
                    activity?.finish()
                }
       }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun loadUserDetails() {
        bindingClassProf.userName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes : ByteArray? = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap : Bitmap? = bytes?.let { BitmapFactory.decodeByteArray(bytes, 0, it.size) }
        bindingClassProf.profileImage.setImageBitmap(bitmap)
    }

    private fun clearFile(fileName: String) {
        sharedPreferences = requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()
        edit.clear()
        edit.apply()
    }

    private fun showToast(message:String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}