package it.projector.lamba.projector.model

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import it.projector.lamba.projector.R
import it.projector.lamba.projector.data.Resource
import it.projector.lamba.projector.utilility.inflate
import kotlinx.android.synthetic.main.project_resource_item.view.*


/**
 * Created by lamba on 21/02/2018.
 */

class ResourcesAdapter(private val items: ArrayList<Resource> = ArrayList()): RecyclerView.Adapter<ResourcesAdapter.ResourceViewHolder>() {

    fun add(r: Resource) {
        items.add(r)
        notifyItemInserted(items.size-1)
    }

    class ResourceViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bind(r: Resource) {
            itemView.project_resource_link.text = r.link
            itemView.project_resource_title.text = r.title
            itemView.project_resource_icon.setImageResource(iconProvider(r.icon))
        }

        private fun iconProvider(icon: String): Int {
            return when(icon){
                "bitbucket" -> R.drawable.ic_bitbucket_black_24dp
                "git"  -> R.drawable.ic_git_black_24dp
                "github" -> R.drawable.ic_github_black_24dp
                else -> R.drawable.ic_android_black_24dp
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ResourceViewHolder(parent.inflate(R.layout.project_resource_item))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ResourcesAdapter.ResourceViewHolder, position: Int) = holder.bind(items[position])

}


