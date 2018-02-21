package it.projector.lamba.projector.data

/**
 * Created by lamba on 20/02/2018.
 */
data class User(val id: String, val name: String, val surname: String, val email: String,
                val badgeNumber: String, var project: String?)