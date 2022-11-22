package com.morphylix.android.countries.presentation

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.morphylix.android.countries.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

private const val SUGGESTIONS_AMOUNT = 4
private const val FLAGS_URL = "https://countryflagsapi.com/png/"
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var suggestions: MutableList<String> = mutableListOf()
    private var _searchView: SearchView? = null
    private val searchView: SearchView get() = _searchView!!
    private lateinit var countryNameTextView: TextView
    private lateinit var capitalTextView: TextView
    private lateinit var countryImageView: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countryNameTextView = findViewById(R.id.country_name_text_view)
        capitalTextView = findViewById(R.id.capital_text_view)
        countryImageView = findViewById(R.id.country_image_view)
        progressBar = findViewById(R.id.main_progress_bar)

        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        mainActivityViewModel.viewModelScope.launch {
            mainActivityViewModel.mainActivityState.collect { state ->
                when (state) {

                    is MainActivityState.Loading -> {
                        startLoading()
                    }

                    is MainActivityState.SuggestionsSuccess -> {
                        suggestions.clear()
                        for (i in state.suggestions.indices) {
                            if (i > SUGGESTIONS_AMOUNT) break
                            suggestions.add(state.suggestions[i].name)
                        }
                        updateCursor()
                    }

                    is MainActivityState.CapitalSuccess -> {
                        capitalTextView.text = state.capital
                    }

                    is MainActivityState.Cnn3Success -> {
                        postFlagImage(state.cnn3)
                    }

                    is MainActivityState.Synchronize -> {
                        finishLoading()
                    }

                    is MainActivityState.Error -> {

                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)

        val searchItem = menu.findItem(R.id.menu_item_search_view)
        _searchView = searchItem.actionView as SearchView
        val cursorAdapter = createCursorAdapter()
        searchView.suggestionsAdapter = cursorAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mainActivityViewModel.searchCountries(query ?: "")
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                mainActivityViewModel.searchCountries(query ?: "")
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView.setQuery(selection, false)

                mainActivityViewModel.setLoadingState()
                countryNameTextView.text = selection
                mainActivityViewModel.getCapital(selection)
                mainActivityViewModel.getCnn3(selection)


                return true
            }

        })

        return true
    }

    private fun createCursorAdapter(): SimpleCursorAdapter {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.search_item)
        return SimpleCursorAdapter(
            this,
            R.layout.search_item,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
    }

    private fun updateCursor() {
        val cursor =
            MatrixCursor(
                arrayOf(
                    BaseColumns._ID,
                    SearchManager.SUGGEST_COLUMN_TEXT_1
                )
            )
        suggestions.forEachIndexed { index, suggestion ->
            cursor.addRow(arrayOf(index, suggestion))
        }
        searchView.suggestionsAdapter.changeCursor(cursor)
    }

    private fun postFlagImage(cnn3: String) {

        Log.i(TAG, "cnn3 is $cnn3")
        val url = FLAGS_URL + cnn3


        val target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                countryImageView.setImageBitmap(bitmap)
                mainActivityViewModel.synchronize()
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }
        }
        Picasso.with(this)
            .load(url)
            .placeholder(com.google.android.material.R.drawable.design_password_eye)
            .error(com.google.android.material.R.drawable.design_password_eye)
            .into(target)
    }


    private fun startLoading() {
        progressBar.visibility = View.VISIBLE
        countryImageView.visibility = View.GONE
        countryNameTextView.visibility = View.GONE
        capitalTextView.visibility = View.GONE
    }

    private fun finishLoading() {
        progressBar.visibility = View.GONE
        countryImageView.visibility = View.VISIBLE
        countryNameTextView.visibility = View.VISIBLE
        capitalTextView.visibility = View.VISIBLE
    }
}