package com.learning.messenger.messages

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.learning.messenger.R
import com.learning.messenger.models.ChatMessage
import com.learning.messenger.registerLogin.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
	val adapter = GroupAdapter<GroupieViewHolder>()
	var toUser: User? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_chat_log)
		toUser = intent.getParcelableExtra<User>("USER_KEY")
		supportActionBar?.title = toUser?.userName
		recyclerview_chat_log.adapter = adapter
		listenForMessages()
		send_button_chat_log.setOnClickListener {
			performSendMessage()
		}
	}
	private  fun listenForMessages() {
		val fromId = FirebaseAuth.getInstance().uid ?: return
		val toId = toUser?.uid ?: return
		val ref = FirebaseDatabase.getInstance()
			.getReference("/user-messages/$fromId/$toId")
		ref.addChildEventListener(object: ChildEventListener {
			override fun onCancelled(p0: DatabaseError) {}
			override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
			override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
			override fun onChildRemoved(p0: DataSnapshot) {}
			override fun onChildAdded(p0: DataSnapshot, p1: String?) {
				val message = p0.getValue(ChatMessage::class.java) ?: return
				Log.d("ChatLog", message?.text)
				if (message.fromId == FirebaseAuth.getInstance().uid) {
					val fromUser = LatestMessagesActivity.selectedUser ?: return
					adapter.add(ChatFromItem(message?.text, fromUser!!))
				} else {
					adapter.add(ChatToItem(message?.text, toUser!!))
				}
			}
		})
	}
	private fun performSendMessage() {
		val text = new_message_edit_text_chat_log.text.toString()
		val fromId = FirebaseAuth.getInstance().uid ?: return
		val toId = toUser!!.uid.toString()
		val ref = FirebaseDatabase.getInstance()
			.getReference("/user-messages/$fromId/$toId")
			.push()
		val toRef = FirebaseDatabase.getInstance()
			.getReference("/user-messages/$toId/$fromId")
			.push()
		val message = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis()/1000)
		ref.setValue(message).addOnSuccessListener {
			new_message_edit_text_chat_log.text.clear()
			recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
		}
		toRef.setValue(message)
	}
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				onBackPressed()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}
}

class ChatFromItem(val text: String, val user: User) : Item<GroupieViewHolder>() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.itemView.textview_from_user_chat_log.text = text
		val uri = user.avatar
		val targetImageView = viewHolder.itemView.avatar_from_user_chat_log
		Picasso.get().load(uri).into(targetImageView)
	}

	override fun getLayout(): Int {
		return R.layout.chat_from_row
	}
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.itemView.textview_to_user_chat_log.text = text
		val uri = user.avatar
		val targetImageView = viewHolder.itemView.avatar_to_user_chat_log
		Picasso.get().load(uri).into(targetImageView)
	}

	override fun getLayout(): Int {
		return R.layout.chat_to_row
	}
}
