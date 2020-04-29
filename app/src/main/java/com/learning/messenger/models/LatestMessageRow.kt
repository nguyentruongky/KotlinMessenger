package com.learning.messenger.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.learning.messenger.R
import com.learning.messenger.registerLogin.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val message: ChatMessage): Item<GroupieViewHolder>() {
	var chatParner: User? = null
	override fun getLayout(): Int {
		return R.layout.latest_message_row
	}

	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.itemView.message_textview_latest_message_row.text = message.text
		val chatPartnerId: String
		if (message.fromId == FirebaseAuth.getInstance().uid) {
			chatPartnerId = message.toId
		} else {
			chatPartnerId = message.fromId
		}
		val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
		ref.addListenerForSingleValueEvent(object : ValueEventListener {
			override fun onDataChange(p0: DataSnapshot) {
				chatParner = p0.getValue(User::class.java)
				viewHolder.itemView.name_textview_latest_message_row.text = chatParner?.userName
				Picasso.get().load(chatParner?.avatar).into(viewHolder.itemView.avatar_latest_message_row)
			}

			override fun onCancelled(p0: DatabaseError) {}
		})
	}
}