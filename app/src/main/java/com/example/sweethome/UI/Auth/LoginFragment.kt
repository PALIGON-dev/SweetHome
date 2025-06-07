package com.example.sweethome.UI.Auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.sweethome.Data.Remote.User
import com.example.sweethome.R
import com.example.sweethome.UI.Main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val PasswordIn = view.findViewById<EditText>(R.id.password_input)
        val EmailIn = view.findViewById<EditText>(R.id.email_input)
        val LogIn = view.findViewById<Button>(R.id.login_button)
        val RegUp = view.findViewById<Button>(R.id.register_button)
        val Reset = view.findViewById<TextView>(R.id.to_login)
        val auth = FirebaseAuth.getInstance()


        LogIn.setOnClickListener(View.OnClickListener {
            var email = EmailIn.text.toString()
            var password = PasswordIn.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(activity, "You need to fill all info", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val db = FirebaseFirestore.getInstance()

                        firebaseUser?.let { user ->
                            val userData = User(
                                id = user.uid,
                                name = user.displayName ?: "New User",
                                email = user.email ?: "",
                                photoUrl = user.photoUrl?.toString(),
                                projects = emptyList()
                            )

                            db.collection("users")
                                .document(user.uid)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    if (!snapshot.exists()) {
                                        db.collection("users")
                                            .document(user.uid)
                                            .set(userData)
                                            .addOnSuccessListener { Log.d("Firestore", "User created") }
                                            .addOnFailureListener { Log.e("Firestore", "Error creating user", it) }
                                    }
                                }
                        }

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(activity, "Wrong Email or password", Toast.LENGTH_SHORT).show()
                        EmailIn.text.clear()
                        PasswordIn.text.clear()
                    }
                }
        })

        RegUp.setOnClickListener(View.OnClickListener {
            var email = EmailIn.text.toString()
            var password = PasswordIn.text.toString()
            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(activity, "You need to fill all info", Toast.LENGTH_SHORT).show()
            }
            else {
                if (password.length < 6) {
                    Toast.makeText(
                        activity,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(activity, "Account created!", Toast.LENGTH_SHORT)
                                    .show()
                                EmailIn.text.clear()
                                PasswordIn.text.clear()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Registration failed: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        })

        Reset.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_login_to_reset)
        })
    }
}