package com.example.cocktailspal

import android.graphics.Bitmap
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
import androidx.navigation.Navigation

import com.example.cocktailspal.databinding.FragmentAddCocktailBinding
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
        val view: View = binding.getRoot()
        binding.saveBtn.setOnClickListener { view1 -> }
        binding.cancellBtn.setOnClickListener { view1 ->
            Navigation.findNavController(view1).popBackStack(
                R.id.userProfileFragment,
                false
            )
        }
        binding.cameraButton.setOnClickListener { view1 -> cameraLauncher?.launch(null) }
        binding.galleryButton.setOnClickListener { view1 -> galleryLauncher?.launch("image/*") }
        return view
    }
}