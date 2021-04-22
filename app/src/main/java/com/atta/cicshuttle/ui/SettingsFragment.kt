package com.atta.cicshuttle.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atta.cicshuttle.LoginActivity
import com.atta.cicshuttle.R
import com.atta.cicshuttle.SessionManager
import com.atta.cicshuttle.databinding.FragmentRoutesBinding
import com.atta.cicshuttle.databinding.FragmentSettingsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
        binding.emailTxt.text = SessionManager.with(requireContext()).getUserEmail()

        return view
    }

    private fun logOut() {
        // Firebase sign out
        auth.signOut()

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