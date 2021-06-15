package com.atta.cicshuttle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.atta.cicshuttle.databinding.ActivityRegisterBinding
import com.atta.cicshuttle.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val TAG = "RegisterActivity"

    private lateinit var db: FirebaseFirestore

    private lateinit var phone:  String
    private lateinit var lastName: String
    private lateinit var firstName: String
    private lateinit var email: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email").toString()
        binding.emailTv.setText(email)

        db = Firebase.firestore

        binding.done.setOnClickListener {
            phone = binding.phoneTv.text.toString()
            lastName = binding.lastNameTv.text.toString()
            firstName = binding.firstNameTv.text.toString()
            if (verify()){
                val user = mapOf("email" to email,
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "phone" to phone,
                        "enabled" to false)
                createAccount(user)
            }
        }
    }

    private fun verify(): Boolean{
        when {
            firstName.isEmpty() -> {
                binding.firstNameTv.error = "Please enter your first name"
                binding.firstNameTv.requestFocus()
                return false
            }
            lastName.isEmpty() -> {
                binding.lastNameTv.error = "Please enter your first name"
                binding.lastNameTv.requestFocus()
                return false
            }
            phone.isEmpty() -> {
                binding.phoneTv.error = "Please enter your first name"
                binding.phoneTv.requestFocus()
                return false
            }
            else -> {

                return true
            }
        }
    }

    private fun createAccount(userMap: Map<String, Any>){

        db.collection("Users").add(userMap)
                .addOnSuccessListener {

                    val user = User()
                    user.id = it.id
                    user.email = userMap["email"].toString()
                    user.firstName = userMap["firstName"].toString()
                    user.lastName = userMap["lastName"].toString()
                    user.phone = userMap["phone"].toString()
                    user.enabled = userMap["enabled"] as Boolean

                    SessionManager.with(this).login(user)
                    if (user.enabled){
                        getRouteData(it.id)
                    }else{
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }.addOnFailureListener {
                    Log.d(TAG, "get failed with ", it)
                    Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
                }
    }

    private fun getRouteData(id: String) {
        if (SessionManager.with(this).getUserId() != "") {
            db.collection("user_route")
                    .whereEqualTo("userId", id)
                    .get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            for (document in it) {

                                val routeId = document.data["routeId"] as String
                                val routeName = document.data["routeName"] as String
                                val driverName = document.data["driverName"] as String
                                val driverId = document.data["driverId"] as String
                                SessionManager.with(this).saveRouteData(routeId, routeName, driverName, driverId)
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }else{
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
        }
    }
}