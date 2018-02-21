package it.projector.lamba.projector

import android.util.Log
import com.beust.klaxon.Klaxon
import com.beust.klaxon.PathMatcher
import com.okta.appauth.android.OktaAppAuth
import it.projector.lamba.projector.data.User
import net.openid.appauth.AuthorizationException
import org.apache.commons.io.IOUtils
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

/**
 * Created by lamba on 20/02/2018.
 */

const val BACKEND_API = "http://ec2-18-217-15-16.us-east-2.compute.amazonaws.com:8080/api"
val TAG = "BackendService"

class BackendService private constructor(){

    companion object Factory{
        lateinit var mOktaAppAuth: OktaAppAuth

        fun init(mOktaAppAuth: OktaAppAuth){
            this@Factory.mOktaAppAuth = mOktaAppAuth
            Log.d(TAG, "init called")
        }

        fun getCurrentUser(onSuccess: (user: User) -> Unit,
                           onFailure: (p0: Int, p1: Exception?) -> Unit){
            Log.d(TAG, "getCurrentUser called")
            mOktaAppAuth.performAuthorizedRequest( object : OktaAppAuth.BearerAuthRequest{

                override fun onSuccess(p0: InputStream) {
                    val json = IOUtils.toString(p0, "UTF-8")
                    Log.d(TAG, "Current user JSON is:\n$json")
                    val jsonObj = JSONObject(json)
                    val id =  jsonObj.getJSONObject("profile").getString("given_name")
                    val name = jsonObj.getJSONObject("profile").getString("name")
                    val email = jsonObj.getJSONObject("profile").getString("email")
                    val badge = jsonObj.getJSONObject("profile").getString("bagde_number")
                    val projectId = jsonObj.getJSONObject("profile").getString("projectId")
                    onSuccess(
                        User(jsonObj.getString("id"),
                                jsonObj.getJSONObject("profile").getString("given_name"),
                                jsonObj.getJSONObject("profile").getString("family_name"),
                                jsonObj.getJSONObject("profile").getString("email"),
                                jsonObj.getJSONObject("profile").getString("bagde_number"), null)
                    )
                }

                override fun onTokenFailure(p0: AuthorizationException) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun createRequest(): HttpURLConnection {
                    Log.d(TAG, "createRequest called")
                    val conn = URL("$BACKEND_API/users/me")
                            .openConnection() as HttpURLConnection
                    conn.instanceFollowRedirects = false
                    return conn
                }

                override fun onFailure(p0: Int, p1: Exception?) {
                    Log.d(TAG, "onFailure called")
                    onFailure(p0, p1)
                }

            })
        }
    }
}