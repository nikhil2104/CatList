package com.intuit.catlist.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intuit.catlist.adapters.CatAdapter
import com.intuit.catlist.vm.CatViewModel
import com.intuit.catlist.R
import com.intuit.catlist.databinding.ActivityCatListBinding
import com.intuit.catlist.persist.CatDatabase
import com.intuit.catlist.persist.SharedPref
import com.intuit.catlist.network.Endpoint

class CatListActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCatListBinding

    private val viewModel: CatViewModel by viewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T: ViewModel> create(modelClass: Class<T>): T {
                val db = CatDatabase.getInstance(this@CatListActivity)
                @Suppress("UNCHECKED_CAST")
                return CatViewModel(resources.getString(R.string.api_key), db, Endpoint.create(),
                    SharedPref(PreferenceManager.getDefaultSharedPreferences(this@CatListActivity))) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = CatAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewModel.cats.observe(this, {
            adapter.submitList(it)
        })
        viewModel.dataLoadCompleted.observe(this, {
            adapter.showLoader(!it)
        })
    }
}