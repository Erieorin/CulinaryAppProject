package com.example.culinaryappproject.ui.profile

import android.os.Bundle
import android.widget.*
import com.example.culinaryappproject.R
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.culinaryappproject.ui.register.AuthActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class ProfileFragment  : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.userEmailTextView)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            tvUserEmail.text = user.email ?: "Не указано"

            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val userName = document.getString("name") ?: "Пользователь"
                    tvUserName.text = userName
                }
        }
        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }
}