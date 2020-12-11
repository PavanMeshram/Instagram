package com.example.instagram

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.instagram.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        val logout_btn = findViewById<Button>(R.id.logout_btn)
        val save_info_profile_btn = findViewById<ImageView>(R.id.save_info_profile_btn)
        val change_image_text_btn = findViewById<TextView>(R.id.change_image_text_btn)

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_image_text_btn.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingsActivity)
        }

        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val profile_image_view_profile_frag =
            findViewById<CircleImageView>(R.id.profile_image_view_profile_frag)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly() {

        val username_profile_frag = findViewById<TextView>(R.id.username_profile_frag)
        val full_name_profile_frag = findViewById<TextView>(R.id.full_name_profile_frag)
        val bio_profile_frag = findViewById<TextView>(R.id.bio_profile_frag)

        when {
            full_name_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Enter Full Name", Toast.LENGTH_LONG)
                    .show()
            }
            username_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Enter User Name", Toast.LENGTH_LONG)
                    .show()
            }
            bio_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Enter Bio", Toast.LENGTH_LONG)
                    .show()
            }
            else -> {
                val userRef =
                    FirebaseDatabase.getInstance().reference.child("Users")
                val userMap = HashMap<String, Any>()
                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account has been Updated", Toast.LENGTH_LONG)
                    .show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun userInfo() {
        val userRef =
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)
        val profile_image_view_profile_frag =
            findViewById<CircleImageView>(R.id.profile_image_view_profile_frag)
        val username_profile_frag = findViewById<TextView>(R.id.username_profile_frag)
        val full_name_profile_frag = findViewById<TextView>(R.id.full_name_profile_frag)
        val bio_profile_frag = findViewById<TextView>(R.id.bio_profile_frag)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
//                if (context != null) {
//                    return
//                }

                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_view_profile_frag)
                    username_profile_frag?.setText(user.getUsername())
                    full_name_profile_frag?.setText(user.getFullname())
                    bio_profile_frag?.setText(user.getBio())
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun uploadImageAndUpdateInfo() {
        val username_profile_frag = findViewById<TextView>(R.id.username_profile_frag)
        val full_name_profile_frag = findViewById<TextView>(R.id.full_name_profile_frag)
        val bio_profile_frag = findViewById<TextView>(R.id.bio_profile_frag)

        when {
            imageUri == null -> {
                Toast.makeText(this, "Select Image", Toast.LENGTH_LONG)
                    .show()
            }
            full_name_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Enter Full Name", Toast.LENGTH_LONG)
                    .show()
            }
            username_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Enter User Name", Toast.LENGTH_LONG)
                    .show()
            }
            bio_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Enter Bio", Toast.LENGTH_LONG)
                    .show()
            }
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, Updating your profile...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] =
                            full_name_profile_frag.text.toString().toLowerCase()
                        userMap["username"] =
                            username_profile_frag.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account has been Updated", Toast.LENGTH_LONG)
                            .show()

                        val intent =
                            Intent(this@AccountSettingsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

}