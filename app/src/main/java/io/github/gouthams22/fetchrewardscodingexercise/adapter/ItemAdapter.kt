package io.github.gouthams22.fetchrewardscodingexercise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.fetchrewardscodingexercise.R
import io.github.gouthams22.fetchrewardscodingexercise.activity.MainActivity.Item

class ItemAdapter(private val items: ArrayList<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemListId: MaterialTextView = itemView.findViewById(R.id.item_list_id)
        val itemName: MaterialTextView = itemView.findViewById(R.id.item_name)
        val itemId: MaterialTextView = itemView.findViewById(R.id.item_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = items[position].name
        holder.itemId.text = items[position].id.toString()
        holder.itemListId.text = items[position].listId.toString()
    }
}