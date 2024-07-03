package io.github.gouthams22.fetchrewardscodingexercise.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.github.gouthams22.fetchrewardscodingexercise.R
import io.github.gouthams22.fetchrewardscodingexercise.activity.MainActivity.ItemList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ItemListAdapter(private val list: ArrayList<ItemList>) :
    RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    // Prevent accidental clicks
    class SafeClickListener(
        private var defaultInterval: Int = 1000,
        private val onSafeCLick: (View) -> Unit
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listId: MaterialTextView = itemView.findViewById(R.id.list_id_textview)
        val itemRecycler: RecyclerView = itemView.findViewById(R.id.recycler_item)
        val expandButton: MaterialButton = itemView.findViewById(R.id.expand_button)
        val columnMaterialCard: MaterialCardView = itemView.findViewById(R.id.column_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_item_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.listId.text = list[position].listId.toString()
        val linearLayoutManager = LinearLayoutManager(holder.itemRecycler.context)
        val itemAdapter = ItemAdapter(list[position].items)
        holder.itemRecycler.apply {
            layoutManager = linearLayoutManager
            adapter = itemAdapter
            setRecycledViewPool(RecyclerView.RecycledViewPool())
        }

        rotateArrow(holder.expandButton)

        // Handle expand and collapse of items
        holder.expandButton.setSafeOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {

                rotateArrow(holder.expandButton)

                TransitionManager.beginDelayedTransition(
                    holder.itemView as ViewGroup,
                    AutoTransition()
                )

                // toggle visibility
                holder.columnMaterialCard.visibility =
                    if (holder.itemRecycler.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                holder.itemRecycler.visibility =
                    if (holder.itemRecycler.visibility == View.VISIBLE) View.GONE else View.VISIBLE

            }
        }
    }

    /**
     * Rotate the arrow of the Button
     * @param button The button to be rotated
     */
    private fun rotateArrow(button: MaterialButton) {
        val degreeAngle = if ((button.rotation == 180f)) 0f else 180f
        button.animate().rotation(degreeAngle)
            .setInterpolator(AccelerateDecelerateInterpolator())
    }
}