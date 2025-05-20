package com.example.sweethome.UI.Auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.sweethome.R
import com.example.sweethome.UI.Main.MainActivity
import com.google.firebase.auth.FirebaseAuth


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
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
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
            if (email.isBlank() || password.isBlank()){
                Toast.makeText(activity, "You need to fill all info", Toast.LENGTH_SHORT).show()
            }
            else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                Toast.makeText(activity, "Account created!", Toast.LENGTH_SHORT).show()
                EmailIn.text.clear()
                PasswordIn.text.clear()
            }
        })

        Reset.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_login_to_reset)
        })
    }
}