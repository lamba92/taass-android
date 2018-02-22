package it.projector.lamba.projector

import android.support.v7.widget.RecyclerView
import android.util.Log
import com.beust.klaxon.Klaxon
import com.beust.klaxon.PathMatcher
import com.okta.appauth.android.OktaAppAuth
import it.projector.lamba.projector.data.Project
import it.projector.lamba.projector.data.Resource
import it.projector.lamba.projector.data.User
import it.projector.lamba.projector.model.ResourcesAdapter
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

        fun getProjectsByTitle(query: String, onSuccess: (projects: ArrayList<Project>) -> Unit,
                               onFailure: ((p0: Int, p1: Exception?) -> Unit)) {
            Log.d(TAG, "getProjectsByTitle called")
            mOktaAppAuth.performAuthorizedRequest( object : OktaAppAuth.BearerAuthRequest{
                override fun onSuccess(p0: InputStream) {
                    val json = JSONObject(IOUtils.toString(p0, "UTF-8")).getJSONObject("_embedded").getJSONArray("projects")
                    Log.d(TAG, "Array lenght is: ${json.length()}")
                    Log.d(TAG, "Projects JSON is:\n$json")
                    val ret = ArrayList<Project>()
                    for (i in 0 until json.length()){
                        Log.d(TAG, "Project number $i is:\n${json.getJSONObject(i)}")
                        ret.add(parseProject(json.getJSONObject(i)))
                    }
                    onSuccess(ret)
                }

                override fun onTokenFailure(p0: AuthorizationException) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun createRequest(): HttpURLConnection {
                    Log.d(TAG, "createRequest called")
                    val conn = URL("$BACKEND_API/projects/search/findByTitleContainingIgnoreCase?title=$query")
                            .openConnection() as HttpURLConnection
                    conn.instanceFollowRedirects = false
                    return conn
                }

                override fun onFailure(p0: Int, p1: Exception?) {
                    onFailure(p0, p1)
                }

            })
        }

        fun getCurrentUser(onSuccess: (user: User) -> Unit,
                           onFailure: ((p0: Int, p1: Exception?) -> Unit)){
            Log.d(TAG, "getCurrentUser called")
            mOktaAppAuth.performAuthorizedRequest( object : OktaAppAuth.BearerAuthRequest{

                override fun onSuccess(p0: InputStream) {
                    val json = IOUtils.toString(p0, "UTF-8")
                    Log.d(TAG, "Current user JSON is:\n$json")
                    onSuccess(parseUser(JSONObject(json)))
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

        fun getUser(id: String, onSuccess: (user: User) -> Unit,
                    onFailure: (p0: Int, p1: Exception?) -> Unit) {
            Log.d(TAG, "getUser called with id: $id")
            mOktaAppAuth.performAuthorizedRequest( object : OktaAppAuth.BearerAuthRequest{
                override fun onSuccess(p0: InputStream) {
                    val json = IOUtils.toString(p0, "UTF-8")
                    Log.d(TAG, "User JSON with id $id is:\n$json")
                    onSuccess(parseUser(JSONObject(json)))
                }

                override fun onTokenFailure(p0: AuthorizationException) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun createRequest(): HttpURLConnection {
                    Log.d(TAG, "createRequest called")
                    val conn = URL("$BACKEND_API/users/$id")
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

        fun getKeynote(id: Long, onSuccess: (resource: Resource) -> Unit,
                       onFailure: (p0: Int, p1: Exception?) -> Unit) =
                getResource(id, "keynotes", onSuccess, onFailure)

        fun getRepositoriy(id: Long, onSuccess: (resource: Resource) -> Unit,
                           onFailure: (p0: Int, p1: Exception?) -> Unit) =
                getResource(id, "repoes", onSuccess, onFailure)

        private fun getResource(id: Long, type: String,
                                onSuccess: (resource: Resource) -> Unit,
                                onFailure: (p0: Int, p1: Exception?) -> Unit){
            Log.d(TAG, "getPresentation called with id: $id")
            mOktaAppAuth.performAuthorizedRequest( object : OktaAppAuth.BearerAuthRequest{
                override fun onSuccess(p0: InputStream) {
                    val json = IOUtils.toString(p0, "UTF-8")
                    Log.d(TAG, "User JSON with id $id is:\n$json")
                    onSuccess(parseResource(JSONObject(json), id))
                }

                override fun onTokenFailure(p0: AuthorizationException) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun createRequest(): HttpURLConnection {
                    Log.d(TAG, "createRequest called")
                    val conn = URL("$BACKEND_API/$type/$id")
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

        private fun parseProject(json: JSONObject): Project {
            val ownersIds = ArrayList<String>()
            val repoesIds = ArrayList<Long>()
            val keynotesIds = ArrayList<Long>()

            for(i in 0 until json.getJSONArray("ownerIds").length()) {
                val id = json.getJSONArray("ownerIds").getString(i)
                ownersIds.add(id)
            }
            for(i in 0 until json.getJSONArray("repoIds").length()) {
                repoesIds.add(json.getJSONArray("repoIds").getLong(i))
            }
            for(i in 0 until json.getJSONArray("keynoteIds").length()) {
                keynotesIds.add(json.getJSONArray("keynoteIds").getLong(i))
            }
            return Project(json.getString("title"), json.getString("description"),
                    ownersIds, repoesIds, keynotesIds)
        }

        private fun parseResource(json: JSONObject, id: Long): Resource {
            return Resource(id,
                    json.getString("title"),
                    json.getString("link"),
                    json.getString("icon")
            )
        }

        private fun parseUser(json: JSONObject): User {
            return User(
                json.getString("id"),
                json.getJSONObject("profile").getString("given_name"),
                json.getJSONObject("profile").getString("family_name"),
                json.getJSONObject("profile").getString("email"),
                json.getJSONObject("profile").getString("bagde_number"), null)
        }
    }
}