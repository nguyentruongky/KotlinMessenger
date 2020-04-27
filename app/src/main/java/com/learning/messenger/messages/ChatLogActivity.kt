package com.learning.messenger.messages

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.learning.messenger.R
import com.learning.messenger.registerLogin.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_chat_log)
		val user = intent.getParcelableExtra<User>("USER_KEY")
		supportActionBar?.title = user.userName
		val adapter = GroupAdapter<GroupieViewHolder>()
		adapter.add(ChatFromItem())
		adapter.add(ChatToItem())
		adapter.add(ChatFromItem())
		adapter.add(ChatToItem())
		adapter.add(ChatToItem())
		adapter.add(ChatFromItem())
		recyclerview_chat_log.adapter = adapter
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

class ChatFromItem : Item<GroupieViewHolder>() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {

	}

	override fun getLayout(): Int {
		return R.layout.chat_from_row
	}
}


class ChatToItem : Item<GroupieViewHolder>() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {

	}

	override fun getLayout(): Int {
		return R.layout.chat_to_row
	}
}
