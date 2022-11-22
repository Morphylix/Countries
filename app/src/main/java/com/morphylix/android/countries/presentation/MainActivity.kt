package com.morphylix.android.countries.presentation

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.Menu
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.morphylix.android.countries.R
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        countryNameTextView = findViewById(R.id.country_name_text_view)
        capitalTextView = findViewById(R.id.capital_text_view)
        countryImageView = findViewById(R.id.country_image_view)

        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        mainActivityViewModel.viewModelScope.launch {
            mainActivityViewModel.suggestionsState.collect { state ->
                when (state) {

                    is MainActivitySuggestionsState.Loading -> {

                    }

                    is MainActivitySuggestionsState.SuggestionsSuccess -> {
                        suggestions.clear()
                        for (i in state.suggestions.indices) {
                            if (i > SUGGESTIONS_AMOUNT) break
                            suggestions.add(state.suggestions[i].name)
                        }
                        updateCursor()
                        mainActivityViewModel.setLoadingState()
                    }

                    is MainActivitySuggestionsState.CapitalSuccess -> {
                        capitalTextView.text = state.capital
                    }

                    is MainActivitySuggestionsState.Cnn3Success -> {
                        postFlagImage(state.cnn3)
                    }

                    is MainActivitySuggestionsState.Error -> {

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

    fun postFlagImage(cnn3: String) {

        Log.i(TAG, "cnn3 is $cnn3")
        val url = FLAGS_URL + cnn3

        Picasso.with(this)
            .load(url)
            .placeholder(com.google.android.material.R.drawable.design_password_eye)
            .error(com.google.android.material.R.drawable.design_password_eye)
            .into(countryImageView)
    }
}