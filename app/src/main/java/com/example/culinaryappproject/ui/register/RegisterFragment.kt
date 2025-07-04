package com.example.culinaryappproject.ui.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.culinaryappproject.R
import com.example.culinaryappproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val etName = view.findViewById<EditText>(R.id.etName)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.length < 6) {
                Toast.makeText(
                    requireContext(),
                    "Проверьте корректность данных",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Регистрация пользователя через FirebaseAuth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid ?: return@addOnSuccessListener
                    val user = User(id = uid, name = name, email = email)

                    // Сохранение данных пользователя в Firestore
                    db.collection("users").document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Регистрация успешна!",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Переход на главный экран
                            (requireActivity() as AuthActivity).navigateToMainApp()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Register", "Ошибка Firestore: $e")
                            Toast.makeText(
                                requireContext(),
                                "Ошибка сохранения данных",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Ошибка регистрации: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
        btnLogin.setOnClickListener {
            (requireActivity() as AuthActivity).navigateToLogin()
        }
    }
}

