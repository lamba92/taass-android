package it.projector.lamba.projector.data

/**
 * Created by lamba on 21/02/2018.
 */
data class Project(val title: String, val description: String, val ownersIds: List<String>?,
                   val reposIds: List<Long>?, val keynotesIds: List<Long>?)