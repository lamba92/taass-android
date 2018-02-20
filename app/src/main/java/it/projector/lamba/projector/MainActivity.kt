package it.projector.lamba.projector

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.okta.appauth.android.OktaAppAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import net.openid.appauth.AuthorizationException
import android.app.PendingIntent
import android.content.Intent
import android.view.View
import it.projector.lamba.projector.R.id.cool_button
import kotlinx.android.synthetic.main.content_main.*
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


const val OKTA_REQUEST_CODE = 777
const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mOktaAuth: OktaAppAuth
    private lateinit var backendService: BackendService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOktaAuth = OktaAppAuth.getInstance(this)
        mOktaAuth.init(this,
                object : OktaAppAuth.OktaAuthListener {
                    override fun onSuccess() {
                        val login = mOktaAuth.isUserLoggedIn
                        if (!login) {
                            val completionIntent = Intent(this@MainActivity, MainActivity::class.java)
                            val cancelIntent = Intent(this@MainActivity, MainActivity::class.java)
                            cancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                            mOktaAuth.login(this@MainActivity,
                                    PendingIntent.getActivity(this@MainActivity, OKTA_REQUEST_CODE, completionIntent, 0),
                                    PendingIntent.getActivity(this@MainActivity, OKTA_REQUEST_CODE, cancelIntent, 0)
                            )
                        }
                    }

                    override fun onTokenFailure(ex: AuthorizationException) {
                        this@MainActivity.finishAndRemoveTask()
                    }
                })
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_exit -> {
                finishAndRemoveTask()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
