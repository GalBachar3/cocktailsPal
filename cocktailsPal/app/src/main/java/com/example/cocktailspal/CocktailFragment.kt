package com.example.cocktailspal

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cocktailspal.databinding.FragmentCocktailBinding
import com.example.cocktailspal.model.cocktail.Cocktail
import com.squareup.picasso.Picasso

class CocktailFragment : Fragment() {
    var binding: FragmentCocktailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCocktailBinding.inflate(inflater, container, false)
        val view: View = binding!!.root

        val cocktail: Cocktail? = CocktailFragmentArgs.fromBundle(requireArguments()).cocktail

        if(cocktail!=null) {
            if (cocktail.photo != null) {
                val bitmap =
                    BitmapFactory.decodeByteArray(cocktail.photo, 0, cocktail.photo.size)
                binding!!.cocktailImage.setImageBitmap(bitmap)
            } else if (cocktail.imgUrl != null && cocktail.imgUrl!!.length > 5) {
                Picasso.get().load(cocktail.imgUrl).placeholder(R.drawable.chef_avatar).into(binding!!.cocktailImage);
            }
            else{
                binding?.cocktailImage?.setImageResource(R.drawable.chef_avatar)
            }
            binding?.cocktailName?.setText(cocktail.name)
            binding?.cocktailCategory?.setText(cocktail.category)
            binding?.cocktailInstructions?.setText(cocktail.instructions)
            //binding?.cocktailIngredients?.setText(cocktail.ingredients);
            binding?.cocktailUserName?.setText(cocktail.username)
        }

        return view
    }
}