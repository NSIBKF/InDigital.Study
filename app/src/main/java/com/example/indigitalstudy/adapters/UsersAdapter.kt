package com.example.indigitalstudy.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.indigitalstudy.databinding.ItemContainerUserBinding
import com.example.indigitalstudy.listeners.UserListener
import com.example.indigitalstudy.models.User


class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private var users: List<User>
    private var userListener: UserListener


    constructor (users: List<User>, userListener: UserListener) {
        this.users = users
        this.userListener = userListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemContainerUserBinding: ItemContainerUserBinding = ItemContainerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(itemContainerUserBinding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(var binding: ItemContainerUserBinding): RecyclerView.ViewHolder(binding.root) {

        fun setUserData(user: User) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email
            binding.ImageProfile.setImageBitmap(getUserImage(user.image))
            binding.root.setOnClickListener {
                userListener.onUserClicked(user)
            }
        }

        private fun getUserImage(encodedImage: String): Bitmap {
            val bytes : ByteArray? = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)
        }

    }
}