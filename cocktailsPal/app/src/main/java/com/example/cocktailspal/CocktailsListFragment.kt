package com.example.cocktailspal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        // Inflate the layout for this fragment
        binding = FragmentCocktailsListBinding.inflate(inflater, container, false)
        val view: View = binding!!.getRoot()
        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.setLayoutManager(LinearLayoutManager(context))
        adapter = CocktailRecyclerAdapter(layoutInflater, viewModel?.data?.value)
        binding?.recyclerView?.setAdapter(adapter)

//        adapter.setOnItemClickListener(new RecipeRecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int pos) {
//                Log.d("TAG", "Row was clicked " + pos);
//                Recipe st = viewModel.getData().getValue().get(pos);
////                StudentsListFragmentDirections.ActionStudentsListFragmentToBlueFragment action = StudentsListFragmentDirections.actionStudentsListFragmentToBlueFragment(st.name);
////                Navigation.findNavController(view).navigate(action);
//            }
//        });
        binding?.progressBar?.setVisibility(View.GONE)
        viewModel?.data?.observe(viewLifecycleOwner) { list -> adapter?.setDataa(list as List<Cocktail>?) }
        CocktailModel.instance().EventListLoadingState.observe(viewLifecycleOwner) { status ->
            binding!!.swipeRefresh.setRefreshing(
                status === CocktailModel.LoadingState.LOADING
            )
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