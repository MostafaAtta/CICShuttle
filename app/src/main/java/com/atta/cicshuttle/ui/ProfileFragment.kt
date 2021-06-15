package com.atta.cicshuttle.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.atta.cicshuttle.R
import com.atta.cicshuttle.SessionManager
import com.atta.cicshuttle.databinding.FragmentProfileBinding
import com.atta.cicshuttle.databinding.FragmentSettingsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        db = Firebase.firestore

        binding.emailTv.setText(context?.let { SessionManager.with(it).getEmail() })
        binding.firstNameTv.setText(context?.let { SessionManager.with(it).getFirstName() })
        binding.lastNameTv.setText(context?.let { SessionManager.with(it).getLastName() })
        binding.phoneTv.setText(context?.let { SessionManager.with(it).getPhone() })
        binding.collegeIdTv.setText(context?.let { SessionManager.with(it).getCollegeId() })
        if (context?.let { SessionManager.with(it).getRouteName().isEmpty() }!!){
            binding.routeGroupe.visibility = View.GONE
        }else{
            binding.crRouteName.text = context?.let { SessionManager.with(it).getRouteName() }
        }


        if (context?.let { SessionManager.with(it).isEnabled() }!!){
            binding.enabledText.text = getString(R.string.enabled)
            binding.enabledText.setTextColor(resources.getColor(R.color.green))
        }else{
            binding.enabledText.text = getString(R.string.disabled)
            binding.enabledText.setTextColor(resources.getColor(R.color.red))
        }


        binding.done.setOnClickListener {
            updateAcc()
        }

        return view
    }

    private fun updateAcc() {
        val acc = mapOf("firstName" to binding.firstNameTv.text.toString(),
            "lastName" to binding.lastNameTv.text.toString(),
            "phone" to binding.phoneTv.text.toString())
        context?.let { SessionManager.with(it).getUserId() }?.let {
            db.collection("Users")
                .document(it)
                .update(acc)
                .addOnSuccessListener {

                    Toast.makeText(context, getString(R.string.done), Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_LONG).show()

                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }
}