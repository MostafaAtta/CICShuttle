package com.atta.cicshuttle.ui

import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.atta.cicshuttle.LoginActivity
import com.atta.cicshuttle.R
import com.atta.cicshuttle.SessionManager
import com.atta.cicshuttle.SplashScreenActivity
import com.atta.cicshuttle.databinding.FragmentSettingsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class SettingsFragment : Fragment(), View.OnClickListener {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }!!

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.logout.setOnClickListener(this)
        //binding.emailTxt.text = SessionManager.with(requireContext()).getUserEmail()
        binding.sosImg.setOnClickListener {
            showSosDialog()
        }

        binding.profileCard.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(SettingsFragmentDirections.actionNavigationSettingsToProfileFragment())
        }

        binding.languageCard.setOnClickListener {
            showLanguageDialog()
        }

        return view
    }


    private fun showSosDialog() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.sos_layout)
        val policeBtn = dialog?.findViewById(R.id.police_btn) as Button
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

    private fun showLanguageDialog() {

        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.language_layout)
        val arBtn = dialog?.findViewById(R.id.ar_btn) as Button
        val enBtn = dialog.findViewById(R.id.en_btn) as Button
        val closeImg = dialog.findViewById(R.id.close_img) as ImageView
        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        arBtn.setOnClickListener {
            changeLanguage("ar")
            dialog.dismiss()
        }
        enBtn.setOnClickListener {
            changeLanguage("en")
            dialog.dismiss() }
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_bg)
        dialog.show()
    }

    private fun changeLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        context?.let { SessionManager.with(it).setLanguage(language) }
        val intent = Intent(context, SplashScreenActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun logOut() {
        // Firebase sign out
        auth.signOut()

        context?.let { SessionManager.with(it).logout() }

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener {
            if (it.isSuccessful){
                val intent = Intent(context, LoginActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.logout -> logOut()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}