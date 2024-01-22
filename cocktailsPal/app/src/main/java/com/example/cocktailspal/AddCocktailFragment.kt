package com.example.cocktailspal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.example.cocktailspal.databinding.FragmentAddCocktailBinding
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.model.cocktail.CocktailApiModel
import com.example.cocktailspal.model.cocktail.CocktailApiReturnObj
import com.example.cocktailspal.model.cocktail.CocktailModel
import com.example.cocktailspal.model.user.UserModel
import java.io.InputStream


class AddCocktailFragment : Fragment() {

    lateinit var  binding: FragmentAddCocktailBinding
    var cameraLauncher: ActivityResultLauncher<Void>? = null
    var galleryLauncher: ActivityResultLauncher<String>? = null
    var isAvatarSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentActivity: FragmentActivity? = activity
        parentActivity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.removeItem(R.id.addCocktailFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, this, Lifecycle.State.RESUMED)
        cameraLauncher = registerForActivityResult<Void, Bitmap>(
            ActivityResultContracts.TakePicturePreview(),
            object : ActivityResultCallback<Bitmap?> {
                override fun onActivityResult(result: Bitmap?) {
                    if (result != null) {
                        binding.cocktailImg.setImageBitmap(result)
                        isAvatarSelected = true
                    }
                }
            })
        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent(),
            object : ActivityResultCallback<Uri?> {
                override fun onActivityResult(result: Uri?) {
                    if (result != null) {
                        binding.cocktailImg.setImageURI(result)
                        isAvatarSelected = true
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddCocktailBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.saveBtn.setOnClickListener { view1 ->
            val name = binding.nameEt.text.toString()
            val category = binding.categoryEt.text.toString()
            val instructions = binding.instructionsEt.text.toString()
            val ingredients: String = binding.instructionsEt.text.toString()
            val userId: String? = UserModel.instance().userProfileDetails?.id
            val imgUrl: String? = binding.cocktailImg.tag as? String

            if (isCocktailFormValid(name, category,instructions)) {
                val cocktail = Cocktail(name, category, instructions, ingredients, userId, imgUrl)
                if (isAvatarSelected) {
                    binding.cocktailImg.setDrawingCacheEnabled(true)
                    binding.cocktailImg.buildDrawingCache()
                    val bitmap =
                        (binding.cocktailImg.drawable as BitmapDrawable).bitmap
                    CocktailModel.instance().uploadImage(cocktail.name, bitmap) { url ->
                        if (url != null) {
                            cocktail.imgUrl = url.toString()
                        }
                        CocktailModel.instance()
                            .addCocktail(cocktail) { findNavController(view1).popBackStack() }
                    }
                } else {
                    CocktailModel.instance()
                        .addCocktail(cocktail) { findNavController(view1).popBackStack() }
                }
            }
        }

        binding.cancellBtn.setOnClickListener { view1 ->
            Navigation.findNavController(view1).popBackStack(
                R.id.userProfileFragment,
                false
            )
        }
        binding.cameraButton.setOnClickListener { cameraLauncher?.launch(null) }
        binding.galleryButton.setOnClickListener { galleryLauncher?.launch("image/*") }

        binding.generateCocktailBtn.setOnClickListener { view1 ->
            val data: LiveData<CocktailApiReturnObj> = CocktailApiModel.instance().randomCocktail
            data.observe(
                viewLifecycleOwner,
                Observer<CocktailApiReturnObj> { cocktail: CocktailApiReturnObj ->
                    binding.nameEt.setText(cocktail.name)
                    binding.categoryEt.setText(cocktail.category)
                    binding.instructionsEt.setText(cocktail.instructions)
//                    binding.ingredientsEt.setText(cocktail.ingredients)
                    val imgData: LiveData<InputStream> =
                        CocktailApiModel.instance().getImg(cocktail.imagePath)
                    imgData.observe(
                        viewLifecycleOwner
                    ) { imgStream: InputStream? ->
                        isAvatarSelected = true
                        binding.cocktailImg.setImageBitmap(BitmapFactory.decodeStream(imgStream))
                    }
                })
        }

        return view
    }

    private fun isCocktailFormValid(
        name: String,
        category: String,
        instructions: String
    ): Boolean {
        var valid = true
        if (name.isEmpty()) {
            binding.nameEt.error = "cocktail name is required"
            binding.nameEt.requestFocus()
            valid = false
        }
        if (category.isEmpty()) {
            binding.categoryEt.error = "cocktail category is required"
            binding.categoryEt.requestFocus()
        }
        if (instructions.isEmpty()) {
            binding.instructionsEt.error = "cocktail instructions is required"
            binding.instructionsEt.requestFocus()
        }
        return valid
    }
}