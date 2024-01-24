package com.example.cocktailspal

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailspal.CocktailRecyclerAdapter.OnItemClickListener
import com.example.cocktailspal.databinding.CocktailRowBinding
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.utils.StringUtils
import com.squareup.picasso.Picasso

class CocktailViewHolder(
    itemView: View, listener: OnItemClickListener?,
    data: List<Cocktail>?
) : RecyclerView.ViewHolder(itemView) {
    var binding: CocktailRowBinding
    var data: List<Cocktail>?
    var avatarImage: ImageView? = null

    init {
        binding = CocktailRowBinding.bind(itemView)
        this.data = data
        itemView.setOnClickListener {
            val pos = adapterPosition
            listener!!.onItemClick(pos)
        }
    }

    fun bind(cocktail: Cocktail, pos: Int) {
        binding.cocktailRowNameTv.setText(cocktail.name)
        binding.cocktailRowCategoryTv.setText(cocktail.category)
        binding.cocktailRowUserTv.setText(cocktail.userName)
        if (cocktail.photo != null) {
            val bitmap =
                BitmapFactory.decodeByteArray(cocktail.photo, 0, cocktail.photo.size)
            binding.cocktailRowAvatarImg.setImageBitmap(bitmap)
        } else if (StringUtils.isBlank(cocktail.imgUrl)) {
            Picasso.get().load(cocktail.imgUrl).placeholder(R.drawable.chef_avatar)
                .into(binding.cocktailRowAvatarImg)
        }
            else {
            binding.cocktailRowAvatarImg.setImageResource(R.drawable.chef_avatar)
        }
    }
}

class CocktailRecyclerAdapter(var inflater: LayoutInflater, data: List<Cocktail?>?) :
    RecyclerView.Adapter<CocktailViewHolder>() {
    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }

    var data: List<Cocktail>? = data as List<Cocktail>?
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        this.data = data as List<Cocktail>?
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
