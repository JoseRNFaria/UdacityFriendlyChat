package com.google.firebase.udacity.friendlychat.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.utils.Constants
import kotlinx.android.synthetic.main.activity_start.*

class StartView : AppCompatActivity() {

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        firebaseAuth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                login_button.isEnabled = false
                goToNextActivity(user.displayName!!)
            } else {
                login_button.isEnabled = true
                firebaseAuth.removeAuthStateListener(authStateListener)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)

        login_button.setOnClickListener {
            val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(), Constants.RC_SIGN_IN)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                firebaseAuth.addAuthStateListener(authStateListener)
            }
        }
    }

    private fun goToNextActivity(displayName: String) {
        val intent = Intent(this, RoomView::class.java)
        intent.putExtra(Constants.USERNAME_PARAM, displayName)
        startActivity(intent)
        finish()
    }

}
