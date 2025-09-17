package com.example.culinaryappproject.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TagAdapter(
    private val tags: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private var selectedTag: String? = null

    fun setSelectedTag(tag: String?) {
        selectedTag = tag
        notifyDataSetChanged()
    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagText: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(tag: String) {
            tagText.text = tag
            val isSelected = tag == selectedTag

            tagText.setTextColor(
                if (isSelected) Color.BLACK else Color.parseColor("#AB3F21")
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        holder.tagText.text = tag
        holder.bind(tag)
        holder.itemView.setOnClickListener {
            selectedTag = if (selectedTag == tag) null else tag
            notifyDataSetChanged()
            onClick(selectedTag ?: "")
        }
    }

    override fun getItemCount(): Int = tags.size
}
