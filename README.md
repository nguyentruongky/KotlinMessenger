# KotlinMessenger
Learn how to write a messenger in Kotlin


I started this with instructions from [Brain Voong](https://www.youtube.com/channel/UCuP2vJ6kRutQBfRmdcI92mA), pretty awesome guy. His full instructions is [here](https://www.youtube.com/playlist?list=PL0dzCUj1L5JE-jiBHjxlmXEkQkum_M3R-)

I'm a senior iOS developer, a bit experience in Node.js. I started Android with Ray wenderlich books but I gave up. It made no sense to me. Also tried some tutorial serials in my first language, but no luck. 

This course is really awesome. Thanks Brian. 

Below is the list of what I learnt from this course. 

[Eposode 1](https://www.youtube.com/watch?v=ihJGxFu2u9Q&list=PL0dzCUj1L5JE-jiBHjxlmXEkQkum_M3R-&index=1)
- Create UI from beginning. 
- Connect constraints in ConstraintLayout. Drag and drop, but I prefer setup in code instead. 
- Log message: `Log.d("Your tag - Should be your Activity Name", "Your message is here")`.
- Start other activity with Intent.
```
val intent = Intent(this, LoginActivity::class.java)
startActivity(intent)
```

[Eposode 2](https://www.youtube.com/watch?v=RYyri2W3Tho&list=PL0dzCUj1L5JE-jiBHjxlmXEkQkum_M3R-&index=2)
- Create and login with Firebase.
- Add depedencies into gradle.build.
- Create round corner textview. 
  - Create new drawable resource: Should name it like rounded_radius.xml (we reuse it in future, other control type).
  - Add code. 
  ```
  <shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@android:color/holo_green_dark"/>
    <corners android:radius="25dp"/>
  </shape>
  ```
  - Change background of the control. 
  ```
  android:background="@drawable/rounded_edittext_register_login"
  ```

[Eposode 3](https://www.youtube.com/watch?v=RYyri2W3Tho&list=PL0dzCUj1L5JE-jiBHjxlmXEkQkum_M3R-&index=3)
- Pick an image
  - Show picker activity
    ```
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(intent, 0)
    ```
  - Get result
    ```
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
        val selectedPhotoUri = data.data
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
        val bitmapDrawable = BitmapDrawable(bitmap)
        avatar_imageview_register.setBackgroundDrawable(bitmapDrawable)
      }
    }
    ```

- Upload to Firebase Storage
  ```
  val imageName = UUID.randomUUID().toString()
  val ref = FirebaseStorage.getInstance().getReference("/images/$imageName")
  ref.putFile(selectedPhotoUri!!)
    .addOnSuccessListener {
      val path = it.metadata?.path
      ref.downloadUrl.addOnSuccessListener {
        val url = it.toString()
        saveUserToDb(url)
      }
    }
    .addOnFailureListener {
      Log.d("Register", it.localizedMessage.toString())
    }
  ```

- Create class `User`
  ```
  class  User(val uid: String, val userName: String, val avatar: String)
  ```

- [CircleImageView implementation](https://github.com/hdodenhof/CircleImageView)
  - Add to build.gradle of app level
  ```
  implementation 'de.hdodenhof:circleimageview:3.1.0'
  ```

[Eposode 4](https://www.youtube.com/watch?v=RYyri2W3Tho&list=PL0dzCUj1L5JE-jiBHjxlmXEkQkum_M3R-&index=4)

- Show new screen and don't allow to go back
  ```
    val intent = Intent(this, LatestMessagesActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
  ```

- Set default activity: change code in manifest file. Add `<intent-filter>` and `</intent-filter>` into default activity.
  ```
    <activity android:name=".messages.LatestMessagesActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
  ```

- Show register activity when not logged in. Add code below into onCreate of default activity. 
  ```
    val uid = FirebaseAuth.getInstance().uid
    if (uid == null) {
      val intent = Intent(this, RegisterActivity::class.java)
      intent.flags =
        Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(intent)
    }
  ```

- Add menu 
  - Add resouce menu file. New/
  - Add menu items
  ```
    <item
        android:id="@+id/menu_new_message"
        android:title="New Message"
        app:showAsAction="ifRoom" />
    <item
        android:id="@+id/menu_sign_out"
        android:title="Sign Out"
        app:showAsAction="ifRoom" />
  ```
  - Show menu in code
  ```
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
  ```

- Set LayoutManager in xml instead of in code. 
- Use [Groupie](https://github.com/lisawray/groupie) to adapt to RecyclerView instead of default way. 
```
  class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
      viewHolder.itemView.user_name_textview_new_message.text = user.userName
    }

    override fun getLayout(): Int {
      return R.layout.user_row_new_message
    }
  }
```

- Use [Picasso](https://github.com/square/picasso) to download images. 
```
Picasso.get().load(user.avatar).into(viewHolder.itemView.avatar_imageView_new_message)
```

- Load users from Firebase Real-time database

```
private fun fetchUsers() {
  FirebaseDatabase.getInstance()
    .getReference("/users")
    .addListenerForSingleValueEvent(object : ValueEventListener {
      override fun onDataChange(p0: DataSnapshot) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        p0.children.forEach {
          val user = it.getValue(User::class.java) ?: return
          adapter.add(UserItem(user!!))
        }
        recyclerview_new_message.adapter = adapter
      }

      override fun onCancelled(p0: DatabaseError) {}
    })
}
```

[Eposode 5](https://www.youtube.com/watch?v=RYyri2W3Tho&list=PL0dzCUj1L5JE-jiBHjxlmXEkQkum_M3R-&index=5)

- Change title 
```
supportActionBar.title = "Chat Log"
```

- Back button on the action bar doesn't work. Here how to fix. 
```
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
```

- Pass object from Activity to Activity
  - From Activity
  ```
  val userItem = item as UserItem
  intent.putExtra("USER_KEY", userItem.user.userName)
  startActivity(intent)
  ```
  - To Activity, onCreate
  ```
  val userName = intent.getStringExtra("USER_KEY")
  ```

- Extend a class to Parcelable
  - Add code to build.gradle (app) above `android`

  ```
  androidExtensions {
    experimental = true
  }
  ```

  - Add `@Parcelize` and extend Parcelable to class definition
  ```
  @Parcelize
  class  User(val uid: String, val userName: String, val avatar: String) : Parcelable {
    constructor() : this("", "", "")
  }
  ```
