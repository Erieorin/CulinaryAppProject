package com.example.culinaryappproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culinaryappproject.R
import com.example.culinaryappproject.databinding.FragmentHomeBinding
import com.example.culinaryappproject.models.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.culinaryappproject.receivers.RecipeNotificationReceiver
import java.util.Calendar
import com.example.culinaryappproject.models.User
import com.example.culinaryappproject.models.Review
import com.example.culinaryappproject.models.Recipe
import com.example.culinaryappproject.models.Step
import com.example.culinaryappproject.ui.register.RegisterActivity
import com.example.culinaryappproject.ui.profile.ProfileActivity

class HomeFragment : Fragment() {
    private val recipeViewModel: RecipeViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        userId = user?.uid

        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RecipeAdapter(requireContext(), emptyList(), userId.toString())
        recyclerView.adapter = adapter

        recipeViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateRecipes(recipes)

            // проверяем статус избранного для каждого рецепта
            recipes.forEach { recipe ->
                if (userId != null) {
                    FirestoreRepository.checkIfFavorite(userId!!, recipe.id) { isFavorite ->
                        recipe.isFavorite = isFavorite
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
        recipeViewModel.fetchCombinedRecipes()

        // Поиск
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                recipeViewModel.searchRecipes(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                recipeViewModel.searchRecipes(newText)
                return false
            }
        })
    }
}