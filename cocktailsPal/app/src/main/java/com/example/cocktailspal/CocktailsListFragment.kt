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
import com.example.cocktailspal.model.cocktail.CocktailModel

class CocktailsListFragment : Fragment() {
    var binding: FragmentCocktailsListBinding? = null
    var adapter: CocktailRecyclerAdapter? = null
    var viewModel: CocktailsListFragmentViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        binding = FragmentCocktailsListBinding.inflate(inflater, container, false)
        val view: View = binding!!.getRoot()
        viewModel = ViewModelProvider(this).get(CocktailsListFragmentViewModel::class.java)
        binding!!.recyclerView.setHasFixedSize(true)
        binding!!.recyclerView.setLayoutManager(LinearLayoutManager(context))
        adapter = CocktailRecyclerAdapter(layoutInflater, viewModel!!.data?.value)
        binding!!.recyclerView.setAdapter(adapter)
        adapter!!.setOnItemClickListener(object : CocktailRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
                Log.d("TAG", "Row was clicked $pos")
                val cocktail: Cocktail? = viewModel?.data?.getValue()?.get(pos)
                findNavController(view).navigate(
                    CocktailsListFragmentDirections.actionCocktailsListFragmentToCocktailFragment(
                        cocktail!!
                    )
                )

            }
        })
        binding!!.progressBar.visibility = View.GONE
        viewModel!!.data?.observe(viewLifecycleOwner) {
            list -> adapter!!.data = (list as List<Cocktail>?)
            if (adapter!!.itemCount == 0){
                binding!!.noCocktailsImg.visibility = View.VISIBLE;
            } else {
                binding!!.noCocktailsImg.visibility = View.GONE;
            }
        }
        CocktailModel.instance().EventListLoadingState.observe(viewLifecycleOwner) { status ->
            binding!!.swipeRefresh.isRefreshing = status === CocktailModel.LoadingState.LOADING
        }
        binding!!.swipeRefresh.setOnRefreshListener { reloadData() }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(CocktailsListFragmentViewModel::class.java)
    }

    fun reloadData() {
        CocktailModel.instance().refreshAllCocktails()
    }
}