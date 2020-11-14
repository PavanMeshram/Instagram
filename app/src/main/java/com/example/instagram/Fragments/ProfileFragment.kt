package com.example.instagram.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.instagram.AccountSettingsActivity
import com.example.instagram.R
import com.example.instagram.Fragments.ProfileFragment

class ProfileFragment : Fragment() {

    private var ctx: Context? = null
    private var self: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_profile, container, false)

        ctx = container?.context
        self = LayoutInflater.from(ctx).inflate(R.layout.fragment_profile, container, false)
        val bDaButton = self?.findViewById<Button>(R.id.edit_account_settings_btn)
        bDaButton?.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }
        return self
    }
}