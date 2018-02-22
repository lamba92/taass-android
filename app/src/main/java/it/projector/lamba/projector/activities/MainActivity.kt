package it.projector.lamba.projector.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.okta.appauth.android.OktaAppAuth
import it.projector.lamba.projector.BackendService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import it.projector.lamba.projector.R
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import net.openid.appauth.AuthorizationException
import it.projector.lamba.projector.model.ProjectsAdapter

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val TAG = "MainActivity"
    private val projectsAdapter = ProjectsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        val mOktaAuth = OktaAppAuth.getInstance(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        projects_rv.adapter = projectsAdapter
        val toggle = ActionBarDrawerToggle(
                this@MainActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this@MainActivity)
        mOktaAuth.init(this,
                object : OktaAppAuth.OktaAuthListener {
                    override fun onSuccess() {
                        mOktaAuth.refreshAccessToken(object : OktaAppAuth.OktaAuthListener {
                            @SuppressLint("SetTextI18n")
                            override fun onSuccess() {
                                Log.d(TAG, "refreshAccessToken onSuccess called")
                                BackendService.init(mOktaAuth)
                                BackendService.getCurrentUser(onSuccess = {
                                    nav_header_name.text = "${it.name} ${it.surname}"
                                    nav_header_email.text = it.email
                                }, onFailure = {p0, p1 ->})
                                BackendService.getProjectsByTitle("", {
                                    projectsAdapter.add(it)
                                }, {p0, p1 ->})
                            }

                            override fun onTokenFailure(p0: AuthorizationException) {
                                Log.e(TAG, p0.error)
                            }

                        })
                    }
                    override fun onTokenFailure(ex: AuthorizationException) {
                        Log.d(TAG, ex.error)
                    }
                }
        )
        super.onCreate(savedInstanceState)
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
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
