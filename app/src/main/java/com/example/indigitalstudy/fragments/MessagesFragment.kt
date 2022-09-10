package com.example.indigitalstudy.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Base64InputStream
import android.util.Base64OutputStream
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.DEFAULT_ARGS_KEY
import com.example.indigitalstudy.databinding.FragmentMessagesBinding
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.common.primitives.Bytes
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MessagesFragment : Fragment() {

    private lateinit var binding : FragmentMessagesBinding
    private lateinit var preferenceManager : PreferenceManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMessagesBinding.inflate(layoutInflater)
        preferenceManager = PreferenceManager(context)
        loadUserDetails()

        return binding.root

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes : ByteArray? = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap : Bitmap? = bytes?.let { BitmapFactory.decodeByteArray(bytes, 0, it.size) }
        binding.ImageProfile.setImageBitmap(bitmap)
    }

}