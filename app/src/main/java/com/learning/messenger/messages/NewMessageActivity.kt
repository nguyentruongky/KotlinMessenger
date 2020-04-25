package com.learning.messenger.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.learning.messenger.R
import com.learning.messenger.registerLogin.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_new_message)
		supportActionBar?.title = "Select User"
		fetchUsers()
	}

	private fun fetchUsers() {
		FirebaseDatabase.getInstance()
			.getReference("/users")
			.addListenerForSingleValueEvent(object : ValueEventListener {
				override fun onDataChange(p0: DataSnapshot) {
					val adapter = GroupAdapter<GroupieViewHolder>()
					p0.children.forEach {
						val user = it.getValue(User::class.java)
						if (user != null) {
							adapter.add(
								UserItem(
									user!!
								)
							)
						}
					}
					recyclerview_new_message.adapter = adapter
				}

				override fun onCancelled(p0: DatabaseError) {

				}
			})
	}
}

class UserItem(val user: User) : Item<GroupieViewHolder>() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.itemView.user_name_textview_new_message.text = user.userName
		Picasso.get().load(user.avatar).into(viewHolder.itemView.avatar_imageView_new_message)
	}

	override fun getLayout(): Int {
		return R.layout.user_row_new_message
	}
}