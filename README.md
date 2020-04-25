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
