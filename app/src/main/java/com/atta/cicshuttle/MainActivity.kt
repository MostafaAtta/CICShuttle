package com.atta.cicshuttle

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.atta.cicshuttle.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.seismic.ShakeDetector


class MainActivity : AppCompatActivity() {
    lateinit var appBarConfiguration: AppBarConfiguration

    private val TAG = "MainActivity"

    private lateinit var db: FirebaseFirestore

    private lateinit var sensorManager: SensorManager

    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_home, R.id.navigation_routes, R.id.navigation_settings
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        db = Firebase.firestore

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful){
                Log.w(TAG, "Fetching FCM registration token failed", it.exception)
                return@addOnCompleteListener
            }

            val token = it.result

            Log.d(TAG, token)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

            getTokens(token)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        shakeDetector = ShakeDetector(ShakeDetector.Listener {
            showSosDialog()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (!SessionManager.with(this).isEnabled()){
            menu?.getItem(0)?.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_chat -> {
                val intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setTokens(tokens: MutableList<String>){
        db.collection("Users")
                .document(SessionManager.with(this).getUserId())
                .update(mapOf("tokens" to tokens))
    }

    private fun getTokens(token: String) {
        val userId = SessionManager.with(this).getUserId()
        db.collection("Users")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!

                    if (!user.tokens.contains(token)){
                        user.tokens.add(token)
                        setTokens(user.tokens)
                    }
                }
    }

    private fun showSosDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.sos_layout)
        val policeBtn = dialog.findViewById(R.id.police_btn) as Button
        val fireStationBtn = dialog.findViewById(R.id.fire_station_btn) as Button
        val ambulanceBtn = dialog.findViewById(R.id.ambulance_btn) as Button
        val closeImg = dialog.findViewById(R.id.close_img) as ImageView
        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        policeBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "122")
            startActivity(dialIntent)
            dialog.dismiss()
        }
        fireStationBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "180")
            startActivity(dialIntent)
            dialog.dismiss() }
        ambulanceBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "123")
            startActivity(dialIntent)
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
        dialog.show()

    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start(sensorManager)
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }
}