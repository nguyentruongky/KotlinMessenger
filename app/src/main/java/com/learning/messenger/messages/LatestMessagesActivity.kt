package com.learning.messenger.messages

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.learning.messenger.R
import com.learning.messenger.registerLogin.RegisterActivity
import com.learning.messenger.registerLogin.User

class LatestMessagesActivity : AppCompatActivity() {
	companion object {
		var selectedUser: User? = null
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_latest_messages)
		fetchCurrentUser()
		verifyUserLoggedIn()
	}
	private fun fetchCurrentUser() {
		val uid = FirebaseAuth.getInstance().uid ?: return
		val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
		ref.addListenerForSingleValueEvent(object : ValueEventListener {
			override fun onDataChange(p0: DataSnapshot) {
				selectedUser = p0.getValue(User::class.java)
			}
			override fun onCancelled(p0: DatabaseError) {}
		})
	}
	private fun verifyUserLoggedIn() {
		val uid = FirebaseAuth.getInstance().uid
		if (uid == null) {
			val intent = Intent(this, RegisterActivity::class.java)
			intent.flags =
				Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
			startActivity(intent)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.nav_menu, menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.menu_new_message -> {
				val intent = Intent(this, NewMessageActivity::class.java)
				startActivity(intent)
			}
			R.id.menu_sign_out -> {
				FirebaseAuth.getInstance().signOut()
				val intent = Intent(this, RegisterActivity::class.java)
				intent.flags =
					Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
				startActivity(intent)
			}
		}
		return super.onOptionsItemSelected(item)
	}
}
