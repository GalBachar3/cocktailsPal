package com.example.cocktailspal

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cocktailspal.databinding.FragmentCocktailsListBinding
import com.example.cocktailspal.model.cocktail.Cocktail

class UserCocktailsListFragment : Fragment() {
    var binding: FragmentCocktailsListBinding? = null
    var adapter: CocktailRecyclerAdapter? = null
    var viewModel: UserCocktailsListFragmentViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCocktailsListBinding.inflate(inflater, container, false)
        val view: View = binding!!.getRoot()
        viewModel = ViewModelProvider(this).get(UserCocktailsListFragmentViewModel::class.java)
        binding!!.recyclerView.setHasFixedSize(true)
        binding!!.recyclerView.setLayoutManager(LinearLayoutManager(context))
        adapter = CocktailRecyclerAdapter(layoutInflater, viewModel!!.data?.getValue())
        binding!!.recyclerView.setAdapter(adapter)
        adapter!!.setOnItemClickListener(object : CocktailRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
                Log.d("TAG", "Row was clicked $pos")
                val cocktail: Cocktail = viewModel?.data?.getValue()?.get(pos)!!
                findNavController(view).navigate(UserCocktailsListFragmentDirections.actionUserCocktailsListFragmentToAddEditCocktailFragment(cocktail));
            }
        })
        binding!!.progressBar.setVisibility(View.GONE)
        viewModel!!.data?.observe(viewLifecycleOwner) { list -> adapter!!.data = (list as List<Cocktail>?) }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(UserCocktailsListFragmentViewModel::class.java)
    }
}