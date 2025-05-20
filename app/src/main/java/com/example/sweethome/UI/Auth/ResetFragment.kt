package com.example.sweethome.UI.Auth

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.sweethome.R
import com.google.firebase.auth.FirebaseAuth

class ResetFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val EmailIn = view.findViewById<EditText>(R.id.reset_email_input)
        val BackToLog = view.findViewById<TextView>(R.id.to_login)
        val ResetBtn = view.findViewById<Button>(R.id.send_reset_button)
        val auth = FirebaseAuth.getInstance()

        ResetBtn.setOnClickListener(View.OnClickListener {
            val email = EmailIn.text.toString()
            if (email.isBlank()) {
                Toast.makeText(activity, "Enter email", Toast.LENGTH_SHORT).show()
            }
            else {
            auth.sendPasswordResetEmail(email)
            Toast.makeText(activity, "Reset message is on it's way", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_reset_to_login)
            }
        })

        BackToLog.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_reset_to_login)
        })
    }

}