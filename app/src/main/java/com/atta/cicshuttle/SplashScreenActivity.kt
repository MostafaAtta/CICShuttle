package com.atta.cicshuttle

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        auth = Firebase.auth;


        db = Firebase.firestore

        checkUser()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkUser() {
        val id = SessionManager.with(this).getUserId()
        if (SessionManager.with(this).getUserId() != "") {
            db.collection("Users")
                .document(id)
                .get()
                .addOnSuccessListener {

                    val enabled = it.data?.get("enabled") as Boolean
                    SessionManager.with(this).enable(enabled)

                    if (FirebaseAuth.getInstance().currentUser != null && SessionManager.with(this).isEnabled()){

                        getRouteData()
                    }else{
                        startHandler()
                    }


                }
                .addOnFailureListener {

                    startHandler()
                    Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
                }
        }else{

            startHandler()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getRouteData(){
        if (SessionManager.with(this).getUserId() != "") {
            db.collection("user_route")
                .whereEqualTo("userId", SessionManager.with(this).getUserId())
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (document in it) {
                            val routeId = document.data["routeId"] as String
                            val routeName = document.data["routeName"] as String
                            val driverName = document.data["driverName"] as String
                            val driverId = document.data["driverId"] as String
                            SessionManager.with(this).saveRouteData(routeId, routeName, driverName, driverId)

                            startHandler()
                        }
                    }else{
                        startHandler()
                    }
                }
                .addOnFailureListener {

                    startHandler()
                    Toast.makeText(this, "get failed with  $it", Toast.LENGTH_SHORT).show()
                }
        }else{

            startHandler()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startHandler() {
        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler(Looper.getMainLooper()).postDelayed({
            changeLanguage(SessionManager.with(this).getLanguage())
            val result = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

            if (result != PackageManager.PERMISSION_GRANTED) {
                requestPermission()
            }else{
                val currentUser = auth.currentUser
                if(currentUser == null){
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }


        }, SPLASH_TIME_OUT)// 3000 is the delayed time in milliseconds.
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permission, FINE_LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val currentUser = auth.currentUser
        if(currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun changeLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }

    companion object {
        // This is the loading time of the splash screen
        private const val SPLASH_TIME_OUT:Long = 1000 // 1 sec


        private const val FINE_LOCATION_REQUEST_CODE = 101
    }

}