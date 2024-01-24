package com.example.cocktailspal

import android.app.ProgressDialog
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.example.cocktailspal.databinding.FragmentAddEditCocktailBinding
import com.example.cocktailspal.model.cocktail.Cocktail
import com.example.cocktailspal.model.cocktail.CocktailApiModel
import com.example.cocktailspal.model.cocktail.CocktailApiReturnObj
import com.example.cocktailspal.model.cocktail.CocktailModel
import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserModel
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class AddEditCocktailFragment : Fragment() {

    lateinit var binding: FragmentAddEditCocktailBinding
    var cameraLauncher: ActivityResultLauncher<Void>? = null
    var galleryLauncher: ActivityResultLauncher<String>? = null
    var cocktailParam: Cocktail? = null
    var isAvatarSelected = false
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            cocktailParam = args["cocktail"] as Cocktail?
            setLabel()
        }

        progressDialog = ProgressDialog(activity)
        val parentActivity: FragmentActivity? = activity
        parentActivity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.removeItem(R.id.addEditCocktailFragment)
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
        binding = FragmentAddEditCocktailBinding.inflate(inflater, container, false)
        val view: View = binding.root

        if (cocktailParam != null) {
            setEditCocktailData(cocktailParam!!)
        }

        binding.saveBtn.setOnClickListener { view1 ->
            val name = binding.nameEt.text.toString()
            val category = binding.categoryEt.text.toString()
            val instructions = binding.instructionsEt.text.toString()
            val ingredients: String = binding.instructionsEt.text.toString()
            val user: User? = UserModel.instance().userProfileDetails
            val username: String? = user?.name
            val userId: String? = user?.id

            if (isCocktailFormValid(name, category, instructions)) {
                val cocktail = Cocktail(name, category, instructions, ingredients, userId, username)
                progressDialog?.setMessage("Please wait while your cocktail is being added...")
                progressDialog?.setTitle("Adding Cocktail")
                progressDialog?.setCanceledOnTouchOutside(false)
                progressDialog?.show()

                val executor: Executor =
                    Executors.newSingleThreadExecutor()
                executor.execute {
                    if (isAvatarSelected) {
                        binding.cocktailImg.setDrawingCacheEnabled(true)
                        binding.cocktailImg.buildDrawingCache()
                        val bitmap =
                            (binding.cocktailImg.getDrawable() as BitmapDrawable).bitmap
                        if (CocktailModel.instance().isCocktailNameExists(cocktail.name)) {
                            requireActivity().runOnUiThread {
                                progressDialog!!.dismiss()
                                binding.nameEt.setError("cocktail name already exists")
                                binding.nameEt.requestFocus()
                                openErrorToast(
                                    view,
                                    "cocktail with the same name already exist"
                                )
                            }
                            return@execute
                        }
                        CocktailModel.instance().uploadImage(cocktail.name, bitmap) { url ->
                            if (url != null) {
                                cocktail.imgUrl = url.toString()
                            }
                            addCocktail(view1, cocktail)
                        }
                    } else {
                        addCocktail(view1, cocktail)
                    }
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
            val data: LiveData<CocktailApiReturnObj> =
                CocktailApiModel.instance().randomCocktail
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

    fun addCocktail(view: View, cocktail: Cocktail) {
        CocktailModel.instance().addCocktail(cocktail) {
            findNavController(view).popBackStack()
            progressDialog?.dismiss()
            Toast.makeText(activity, "Cocktail added successfully", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun openErrorToast(view: View, error: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(
            R.layout.toast_layout,
            view.findViewById<View>(R.id.toast_layout_root) as ViewGroup
        )
        val text = layout.findViewById<TextView>(R.id.text)
        text.text = error
        val toast = Toast(requireActivity().applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }

    private fun setLabel() {
        val navController = findNavController(requireActivity(), R.id.navhost)
        val currentDestination = navController.currentDestination
        if (currentDestination!!.id == R.id.addEditCocktailFragment) {
            (requireActivity() as AppCompatActivity).supportActionBar!!.setTitle("Edit Cocktail")
        }
    }

    private fun setEditCocktailData(cocktail: Cocktail) {

        binding.nameEt.setText(cocktail.name)
        binding.categoryEt.setText(cocktail.category)
        binding.instructionsEt.setText(cocktail.instructions)
        //binding.ingredientsEt.setText(cocktail.ingredients)
        if (cocktail.imgUrl != null && cocktail.imgUrl!!.length > 5) {
            Picasso.get().load(cocktail.imgUrl).placeholder(R.drawable.chef_avatar)
                .into(binding.cocktailImg)
        } else {
            binding.cocktailImg.setImageResource(R.drawable.chef_avatar)
        }
        binding.saveBtn.text = "update"
        requireActivity().title = "edit cocktail"
        binding.generateCocktailBtn.setVisibility(View.GONE);
    }

}