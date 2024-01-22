package com.example.cocktailspal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.utils.StringUtils
import com.squareup.picasso.Picasso


class CocktailViewHolder(
    itemView: View,
    listener: CocktailRecyclerAdapter.OnItemClickListener?,
    data: List<Cocktail>?
) : RecyclerView.ViewHolder(itemView) {
    var nameTv: TextView
    var categoryTv: TextView
    var data: List<Cocktail>?
    var avatarImage: ImageView

    init {
        this.data = data
        nameTv = itemView.findViewById<TextView>(R.id.cocktailRow_name_tv)
        categoryTv = itemView.findViewById<TextView>(R.id.cocktailRow_category_tv)
        avatarImage = itemView.findViewById<ImageView>(R.id.cocktailRow_avatar_img)
        itemView.setOnClickListener {
            val pos: Int = getAdapterPosition()
            listener!!.onItemClick(pos)
        }
    }

    fun bind(recipe: Cocktail, pos: Int) {
        nameTv.setText(recipe.name)
        categoryTv.setText(recipe.category)
        if (StringUtils.isBlank(recipe.imgUrl)) {
            Picasso.get().load(recipe.imgUrl).placeholder(R.drawable.avatar).into(avatarImage)
        } else {
            avatarImage.setImageResource(R.drawable.avatar)
        }
    }
}

class CocktailRecyclerAdapter(inflater: LayoutInflater, data: List<Cocktail?>?) :
    RecyclerView.Adapter<CocktailViewHolder?>() {
    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }

    var inflater: LayoutInflater
    var data: List<Cocktail>?
    fun setDataa(data: List<Cocktail>?) {
        this.data = data
        notifyDataSetChanged()
    }

    init {
        this.inflater = inflater
        this.data = data as List<Cocktail>?
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val view: View = inflater.inflate(R.layout.cocktail_row, parent, false)
        return CocktailViewHolder(view, listener, data)
    }

    override fun getItemCount(): Int {
        if (data == null) return 0 else return data!!.size
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        val recipe: Cocktail = data!![position]
        holder.bind(recipe, position)
    }
}
