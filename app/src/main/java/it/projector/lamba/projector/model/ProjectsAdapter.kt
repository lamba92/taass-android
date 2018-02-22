package it.projector.lamba.projector.model

import android.app.Activity
import android.content.Context
import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import it.projector.lamba.projector.BackendService
import it.projector.lamba.projector.R
import it.projector.lamba.projector.data.Project
import it.projector.lamba.projector.utilility.inflate
import kotlinx.android.synthetic.main.expandable_panel_layout.view.*
import kotlinx.android.synthetic.main.project_card.view.*

/**
 * Created by lamba on 21/02/2018.
 */
class ProjectsAdapter(private val items: ArrayList<Project> = ArrayList(), private val activity: Activity): RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    var dummyRemoved = false

    fun add(p: Project) {
        if(!dummyRemoved){
            items.clear()
        }
        items.add(p)
        notifyDataSetChanged()
    }

    fun add(p: List<Project>){
        if(!dummyRemoved){
            items.clear()
        }
        items.addAll(p)
        notifyDataSetChanged()
    }

    class ProjectViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(p: Project, a: Activity){
            itemView.card_project_title.text = p.title
            itemView.card_project_description.text = p.description
            getStudentsNames(p.ownersIds, {
                a.runOnUiThread {
                    itemView.card_project_students.text = it
                }
            })
            getPresentationsAdapter(p.keynotesIds, {
                a.runOnUiThread {
                    itemView.card_project_presentations.adapter = it
                }
            })
            getRepositoriesAdapter(p.reposIds, {
                a.runOnUiThread {
                    itemView.card_project_repositories.adapter = it
                }
            })

        }

        private fun getStudentsNames(list: List<String>?, callback: (users: String) -> Unit) {
            var toReturn = ""
            var i = 0
            list?.forEach({
                BackendService.getUser(it, onSuccess = {
                    synchronized(toReturn, {
                        toReturn+= "${it.name} ${it.surname} (${it.badgeNumber}); "
                        i++
                        if (i == list.size){
                            callback(toReturn)
                        }
                    })
                }, onFailure = {p0, p1 ->
                    synchronized(toReturn, {
                        i++
                        if (i == list.size){
                            callback(toReturn)
                        }
                    })
                })
            })

        }

        private fun getPresentationsAdapter(list: List<Long>?, callback: (resources: ResourcesAdapter) -> Unit) {
            val ad = ResourcesAdapter()
            list?.forEach({
                BackendService.getKeynote(it, {
                    ad.add(it)
                }, {p0, p1 ->

                })
            })
            callback(ad)
        }

        private fun getRepositoriesAdapter(list: List<Long>?, callback: (resources: ResourcesAdapter) -> Unit) {
            val ad = ResourcesAdapter()
            list?.forEach({
                BackendService.getRepositoriy(it, {
                    ad.add(it)
                }, {p0, p1 ->

                })
            })
            callback(ad)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProjectViewHolder(parent.inflate(R.layout.project_card))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) = holder.bind(items[position], activity)


}