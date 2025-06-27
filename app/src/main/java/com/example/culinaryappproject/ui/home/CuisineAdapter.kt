package com.example.culinaryappproject.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R

class CuisineAdapter(
    private val cuisines: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CuisineAdapter.CuisineViewHolder>() {

    private var selectedCuisine: String? = null

    fun setSelectedCuisine(cuisine: String?) {
        selectedCuisine = cuisine
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cuisine, parent, false)
        return CuisineViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuisineViewHolder, position: Int) {
        val cuisine = cuisines[position]
        holder.bind(cuisine)

        holder.itemView.setOnClickListener {
            selectedCuisine = if (selectedCuisine == cuisine) null else cuisine
            notifyDataSetChanged()
            onClick(selectedCuisine ?: "")
        }
    }

    override fun getItemCount(): Int = cuisines.size

    inner class CuisineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cuisineText: TextView = itemView.findViewById(R.id.cuisineName)
        private val cardView: CardView = itemView.findViewById(R.id.cuisineCard)

        fun bind(cuisine: String) {
            cuisineText.text = cuisine
            val isSelected = cuisine == selectedCuisine

            cardView.setCardBackgroundColor(
                if (isSelected) Color.parseColor("#FFB300") else Color.WHITE
            )
            cuisineText.setTextColor(
                if (isSelected) Color.BLACK else Color.parseColor("#AB3F21")
            )
        }
    }
}
