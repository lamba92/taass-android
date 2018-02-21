package it.projector.lamba.projector.activities

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.okta.appauth.android.OktaAppAuth
import net.openid.appauth.AuthorizationException

/**
 * Created by lamba on 20/02/2018.
 */

class LoginActivity: Activity() {

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mOktaAuth = OktaAppAuth.getInstance(this)
        if (mOktaAuth.isUserLoggedIn) {
            Log.i(TAG, "User is already authenticated, proceeding to token activity")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        mOktaAuth.init(this,
                object : OktaAppAuth.OktaAuthListener {
                    override fun onSuccess() {
                        login(mOktaAuth)
                    }

                    override fun onTokenFailure(ex: AuthorizationException) {
                        Log.d(TAG, ex.error)
                    }
                })
    }

    private fun login(mOktaAuth: OktaAppAuth) {
        val login = mOktaAuth.isUserLoggedIn
        if (!login) {
            val completionIntent = Intent(this@LoginActivity, MainActivity::class.java)
            val cancelIntent = Intent(this@LoginActivity, LoginActivity::class.java)
            cancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            mOktaAuth.login(this@LoginActivity,
                    PendingIntent.getActivity(this@LoginActivity, 0, completionIntent, 0),
                    PendingIntent.getActivity(this@LoginActivity, 0, cancelIntent, 0)
            )
        }
    }
}