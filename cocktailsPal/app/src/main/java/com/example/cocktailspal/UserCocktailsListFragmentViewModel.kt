package com.example.cocktailspal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.model.cocktail.CocktailModel
import com.example.cocktailspal.model.user.UserModel

class UserCocktailsListFragmentViewModel : ViewModel() {
    val data: LiveData<List<Cocktail?>?>? =
        CocktailModel.instance().getAllUserCocktails(UserModel.instance().getUserId())
}
