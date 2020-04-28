package com.learning.messenger.messages

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.*
import com.learning.messenger.R
import com.learning.messenger.models.ChatMessage
import com.learning.messenger.registerLogin.RegisterActivity
import com.learning.messenger.registerLogin.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {
	companion object {
		var selectedUser: User? = null
	}

	val adapter = GroupAdapter<GroupieViewHolder>()
	val latestMessagesMap = HashMap<String, ChatMessage>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_latest_messages)
		fetchCurrentUser()
		verifyUserLoggedIn()
		listenForLatestMessages()
		recycler_view_latest_messages.adapter = adapter
	}
	class LatestMessageRow(val message: ChatMessage): Item<GroupieViewHolder>() {
		override fun getLayout(): Int {
			return R.layout.latest_message_row
		}

		override fun bind(viewHolder: GroupieViewHolder, position: Int) {
			viewHolder.itemView.message_textview_latest_message_row.text = message.text
		}
	}
	private  fun refreshRecyclerView() {
		adapter.clear()
		latestMessagesMap.values.forEach {
			adapter.add(LatestMessageRow(it))
		}
	}
	private  fun addMessage(p0: DataSnapshot) {
		val message = p0.getValue(ChatMessage:: class.javaObjectType) ?: return
		latestMessagesMap[p0.key!!] = message
		refreshRecyclerView()
	}
	private fun listenForLatestMessages() {
		val fromId = FirebaseAuth.getInstance().uid ?: return
		val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/$fromId")
		ref.addChildEventListener(object: ChildEventListener {
			override fun onCancelled(p0: DatabaseError) {}
			override fun onChildChanged(p0: DataSnapshot, p1: String?) {
				addMessage(p0)
			}
			override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
			override fun onChildRemoved(p0: DataSnapshot) {}
			override fun onChildAdded(p0: DataSnapshot, p1: String?) {
				addMessage(p0)
			}
		})
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
