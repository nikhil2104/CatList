package com.intuit.catlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intuit.catlist.R
import com.intuit.catlist.activity.DetailActivity
import com.intuit.catlist.databinding.CatItemBinding
import com.intuit.catlist.databinding.ProgressItemBinding
import com.intuit.catlist.model.Cat
import java.lang.StringBuilder

class CatAdapter: PagedListAdapter<Cat, RecyclerView.ViewHolder>(CAT_COMPARATOR) {
    companion object {
        private const val TYPE_ITEM = 1
        private const val TYPE_LOADER = 2

        private val CAT_COMPARATOR = object : DiffUtil.ItemCallback<Cat>() {
            override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    private var showLoader: Boolean = false

    fun showLoader(show: Boolean) {
        showLoader = show
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> CatViewHolder(CatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_LOADER -> ProgressViewHolder(ProgressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CatViewHolder) {
            holder.bindItem(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showLoader && position == itemCount - 1) {
            TYPE_LOADER
        } else {
            TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (showLoader) 1 else 0
    }

    class CatViewHolder(private val binding: CatItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var cat: Cat? = null
        init {
            binding.root.setOnClickListener {
                DetailActivity.start(binding.root.context, cat!!)
            }
        }

        fun bindItem(cat: Cat?) {
            this.cat = cat
            cat?.let {
                Glide.with(binding.catImage)
                    .load(it.url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerInside()
                    .into(binding.catImage)
                val sb = StringBuilder()
                sb.append(it.breed!!.name)
                binding.catName.text = sb.toString()
            }
        }
    }

    class ProgressViewHolder(binding: ProgressItemBinding): RecyclerView.ViewHolder(binding.root)
}