package com.intuit.catlist.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.intuit.catlist.R
import com.intuit.catlist.databinding.ActivityCatDetailBinding
import com.intuit.catlist.model.Cat

class DetailActivity: AppCompatActivity() {
    companion object {
        private const val CAT = "cat"
        fun start(context: Context, cat: Cat) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(CAT, cat)
            context.startActivity(intent)
        }
    }

    private lateinit var detailBinding: ActivityCatDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityCatDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        val cat = intent.getParcelableExtra<Cat>(CAT)

        cat?.let {
            Glide.with(this)
                .load(cat.url)
                .placeholder(R.drawable.ic_launcher_background)
                .into(detailBinding.catImage)

            val breed = cat.breed!!
            detailBinding.descriptionText.text = getString(R.string.description, breed.name,
                breed.temperament, breed.origin,breed.description, breed.lifeSpan)
        }
    }
}