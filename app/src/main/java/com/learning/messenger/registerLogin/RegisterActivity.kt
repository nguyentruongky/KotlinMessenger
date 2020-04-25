package com.learning.messenger.registerLoginimport android.app.Activityimport android.content.Intentimport android.net.Uriimport android.os.Bundleimport android.provider.MediaStoreimport android.util.Logimport android.widget.Toastimport androidx.appcompat.app.AppCompatActivityimport com.google.firebase.auth.FirebaseAuthimport com.google.firebase.database.FirebaseDatabaseimport com.google.firebase.storage.FirebaseStorageimport com.learning.messenger.Rimport com.learning.messenger.messages.LatestMessagesActivityimport kotlinx.android.synthetic.main.activity_main.*import java.util.*class RegisterActivity : AppCompatActivity() {	override fun onCreate(savedInstanceState: Bundle?) {		super.onCreate(savedInstanceState)		setContentView(R.layout.activity_main)		register_button_register.setOnClickListener {			performRegister()		}		login_button_register.setOnClickListener {			val intent = Intent(this, LoginActivity::class.java)			startActivity(intent)		}		avatar_button_register.setOnClickListener {			val intent = Intent(Intent.ACTION_PICK)			intent.type = "image/*"			startActivityForResult(intent, 0)		}	}	var selectedPhotoUri: Uri? = null	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {		super.onActivityResult(requestCode, resultCode, data)		if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {			selectedPhotoUri = data.data			val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)			avatar_imageview_register.setImageBitmap(bitmap)			avatar_button_register.alpha = 0f		}	}	private fun performRegister() {		val email = email_text_edit_register.text.toString()		val username = username_text_edit_register.text.toString()		val password = password_text_edit_register.text.toString()		Log.d("MainActivity", "Email is " + email)		Log.d("MainActivity", "Password is " + password)		if(email.isEmpty() || password.isEmpty()) {			Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()			return		}		FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)			.addOnCompleteListener {				if (!it.isSuccessful) return@addOnCompleteListener				uploadImageToStorage()			}			.addOnFailureListener {				Toast.makeText(this, it.localizedMessage.toString(), Toast.LENGTH_SHORT).show()			}	}	private fun uploadImageToStorage() {		if (selectedPhotoUri == null) return		val imageName = UUID.randomUUID().toString()		val ref = FirebaseStorage.getInstance().getReference("/images/$imageName")		ref.putFile(selectedPhotoUri!!)			.addOnSuccessListener {				val path = it.metadata?.path				ref.downloadUrl.addOnSuccessListener {					val url = it.toString()					saveUserToDb(url)				}			}			.addOnFailureListener {				Log.d("Register", it.localizedMessage.toString())			}	}	private fun saveUserToDb(avatar: String) {		val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""		val user = User(			uid,			username_text_edit_register.text.toString(),			avatar		)		FirebaseDatabase.getInstance()			.getReference("/users/$uid")			.setValue(user)			.addOnSuccessListener {				Log.d("Register", "Save user detail to db")				val intent = Intent(this, LatestMessagesActivity::class.java)				intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)				startActivity(intent)			}	}}class  User(val uid: String, val userName: String, val avatar: String) {	constructor() : this("", "", "")}