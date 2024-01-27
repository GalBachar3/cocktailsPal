package com.example.cocktailspal

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.cocktailspal.databinding.FragmentUserProfileBinding
import com.example.cocktailspal.model.cocktail.CocktailModel
import com.example.cocktailspal.model.user.User
import com.example.cocktailspal.model.user.UserModel
import com.squareup.picasso.Picasso


class UserProfileFragment : Fragment() {
    var binding: FragmentUserProfileBinding? = null
    var user: User? = null
    var cameraLauncher: ActivityResultLauncher<Void>? = null
    var galleryLauncher: ActivityResultLauncher<String>? = null
    var isAvatarSelected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraLauncher = registerForActivityResult<Void, Bitmap>(
            ActivityResultContracts.TakePicturePreview(),
            object : ActivityResultCallback<Bitmap?> {
                override fun onActivityResult(result: Bitmap?) {
                    if (result != null) {
                        binding?.profileImage?.setImageBitmap(result)
                        isAvatarSelected = true
                    }
                }
            })
        galleryLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent(),
            object : ActivityResultCallback<Uri?> {
                override fun onActivityResult(result: Uri?) {
                    if (result != null) {
                        binding?.profileImage?.setImageURI(result)
                        isAvatarSelected = true
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view: View = binding!!.getRoot()
        user = UserModel.instance().userProfileDetails

        if(user != null){
            binding!!.fullName.setText(user?.name)
            binding!!.fullNameProfile.editText?.setText(user?.name)
            CocktailModel.instance().getUserCocktailCount(object : CocktailModel.Listener<Int?> {
                override fun onComplete(data: Int?) {
                    binding?.cocktailCount?.setText(data?.toString() ?: "null")
                }
            })

//            val executor: Executor = Executors.newSingleThreadExecutor()
////            executor.execute { // Perform database operation here
////                val cocktailsCount: Int? = CocktailModel.instance().getUserCocktailCount()
////                binding!!.cocktailCount.text = cocktailsCount.toString()
//            }

            if (user?.avatarUrl != null && user?.avatarUrl?.length!! > 5) {
                Picasso.get().load(user?.avatarUrl).placeholder(R.drawable.avatar)
                    .into(binding?.profileImage)
            } else {
                binding?.profileImage?.setImageResource(R.drawable.avatar)
            }
        }

        binding?.updateProfileBtn?.setOnClickListener {
            binding?.progressBar?.visibility = View.VISIBLE;
            val name: String = binding?.fullNameProfile?.editText?.text.toString()
            if (isFormValid(name)) {
                var bitmap: Bitmap? = null
                user?.name = name
                if (isAvatarSelected) {
                    binding?.profileImage?.setDrawingCacheEnabled(true)
                    binding?.profileImage?.buildDrawingCache()
                    bitmap = (binding?.profileImage?.drawable as BitmapDrawable).bitmap
                }
                UserModel.instance().updateUserProfile(user, bitmap) { task ->
                    if (task != null) {
                        if (task.isSuccessful) {
                            binding?.fullName?.text = user?.name
                            Toast.makeText(
                                activity,
                                "update user profile Successful",
                                Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(activity, "update user profile failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                        binding?.progressBar?.visibility = View.GONE;
                    }
                }
            }
        }

        binding!!.userCocktails.setOnClickListener { view1 ->
            findNavController(view).navigate(
                UserProfileFragmentDirections.actionUserProfileFragmentToUserCocktailsListFragment()
            )
        }

        binding?.cameraButton?.setOnClickListener { view1 -> cameraLauncher?.launch(null) }
        binding?.galleryButton?.setOnClickListener { view1 -> galleryLauncher?.launch("image/*") }
        return view
    }

    fun isFormValid(name: String): Boolean {
        binding?.fullNameProfile?.setError(null)
        if (name.isEmpty()) {
            binding?.fullNameProfile?.setError("Enter full name")
            binding?.fullNameProfile?.requestFocus()
        }
        return !name.isEmpty()
    }
}