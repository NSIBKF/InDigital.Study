package com.example.indigitalstudy.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.example.indigitalstudy.databinding.ActivitySignUpBinding
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.collections.HashMap

class SignUpActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivitySignUpBinding
    lateinit var preferenceManager: PreferenceManager
    private lateinit var encodedImage: String

    private lateinit var image : String
    private lateinit var email : String
    private lateinit var name : String
    private lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)

        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
    }


    private fun setListeners() {
        bindingClass.signInText.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        bindingClass.signUpBtn.setOnClickListener {
            if(isValidSignUpDetails()) {
                //Переходим в одно из активити подтверждения(подтверждаем телефон)
                val intent = Intent(this, SendOTPActivity::class.java)
                intent.putExtra("image", encodedImage)
                intent.putExtra("email", bindingClass.emailInput.text.toString())
                intent.putExtra("name", bindingClass.nameInput.text.toString())
                intent.putExtra("password", bindingClass.passwordInput.text.toString())

                startActivity(intent)
                //signUp()
            }
        }
        bindingClass.layoutImage.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                pickImage.launch(intent)
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signUp() {

            loading(true)
            val database: FirebaseFirestore = FirebaseFirestore.getInstance()
            val user: HashMap<String, Any> = HashMap()
            user[Constants.KEY_NAME] = bindingClass.nameInput.text.toString()
            user[Constants.KEY_EMAIL] = bindingClass.emailInput.text.toString()
            user[Constants.KEY_PASSWORD] = bindingClass.passwordInput.text.toString()
            user[Constants.KEY_IMAGE] = encodedImage
            database.collection(Constants.KEY_COLLECTIONS_USERS)
                .add(user)
                .addOnSuccessListener {
                    loading(false)
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN, true)
                    preferenceManager.putString(Constants.KEY_USER_ID, it.id)
                    preferenceManager.putString(
                        Constants.KEY_NAME,
                        bindingClass.nameInput.text.toString()
                    )
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage)

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK / Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    loading(false)
                    showToast(it.message)
                }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap: Bitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, true)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private var pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            if (it.data != null) {
                val imageUri: Uri? = it.data!!.data
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bindingClass.ImageProfile.setImageBitmap(bitmap)
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isValidSignUpDetails(): Boolean {
        var result = false

        if (bindingClass.nameInput.text.toString().trim().isEmpty()) {
            showToast("Enter name")
        } else if (encodedImage == null) {   //баг с картинкой вызывается тут
            showToast("Enter image")
        } else if (bindingClass.emailInput.text.toString().trim().isEmpty()) {
            showToast("Enter email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(bindingClass.emailInput.text.toString()).matches()) {
            showToast("Enter valid email")
        } else if (bindingClass.passwordInput.text.toString().trim().isEmpty()) {
            showToast("Enter password")
        } else if (bindingClass.ConfirmPassword.text.toString().trim().isEmpty()) {
            showToast("Confirm your password")
        } else if (bindingClass.passwordInput.text.toString() != bindingClass.ConfirmPassword.text.toString()) {
            showToast("Password & Confirm password must be same")
        } else {
            result = true
        }
        return result

    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            bindingClass.signUpBtn.isVisible = false
            bindingClass.progressBarSignUp.isVisible = true
        } else {
            bindingClass.progressBarSignUp.isVisible = false
            bindingClass.signUpBtn.isVisible = true
        }
    }
}