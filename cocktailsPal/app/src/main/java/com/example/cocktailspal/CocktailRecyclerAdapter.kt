package com.example.cocktailspal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailspal.CocktailRecyclerAdapter.OnItemClickListener
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.utils.StringUtils
import com.squareup.picasso.Picasso

class CocktailViewHolder(itemView: View, listener: OnItemClickListener?, data: List<Cocktail>?) :
    RecyclerView.ViewHolder(itemView) {
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
            val pos = adapterPosition
            listener!!.onItemClick(pos)
        }
    }

    fun bind(cocktail: Cocktail, pos: Int) {
        nameTv.text = cocktail.name
        categoryTv.text = cocktail.category
        if (StringUtils.isBlank(cocktail.imgUrl)) {
            Picasso.get().load(cocktail.imgUrl).placeholder(R.drawable.avatar).into(avatarImage)
        } else {
            avatarImage.setImageResource(R.drawable.avatar)
        }
    }
}

class CocktailRecyclerAdapter(var inflater: LayoutInflater, data: List<Cocktail>?) :
    RecyclerView.Adapter<CocktailViewHolder>() {
    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }

    var data: List<Cocktail>?

    @JvmName("setDataList")
    fun setData(data: List<Cocktail>?) {
        this.data = data
        notifyDataSetChanged()
    }

    init {
        this.data = data
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val view: View = inflater.inflate(R.layout.cocktail_row, parent, false)
        return CocktailViewHolder(view, listener, data)
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        val cocktail: Cocktail = data!![position]
        holder.bind(cocktail, position)
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }
}
