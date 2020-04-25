package com.learning.messenger.registerLogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.learning.messenger.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		login_button_login.setOnClickListener {
			performLogin()
		}

		register_button_login.setOnClickListener {
			finish()
		}
	}
	private fun performLogin() {
		val email = email_text_edit_login.text.toString()
		val password = password_text_edit_login.text.toString()
		if (email.isEmpty() || password.isEmpty()) {
			Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
		}
		FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
			.addOnCompleteListener {
				if (!it.isSuccessful) {
					Toast.makeText(this, it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
					return@addOnCompleteListener
				}

				Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
			}
	}
}
