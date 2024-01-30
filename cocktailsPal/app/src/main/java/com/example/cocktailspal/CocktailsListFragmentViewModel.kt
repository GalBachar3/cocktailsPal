package com.example.cocktailspal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.model.cocktail.CocktailModel

class CocktailsListFragmentViewModel : ViewModel() {
    val data: LiveData<List<Cocktail>> = CocktailModel.instance().allCocktails
}
