package com.codepath.android.booksearch.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.android.booksearch.R
import com.codepath.android.booksearch.R.id
import com.codepath.android.booksearch.R.layout
import com.codepath.android.booksearch.adapters.BookAdapter
import com.codepath.android.booksearch.adapters.BookAdapter.OnItemClickListener
import com.codepath.android.booksearch.models.Book
import com.codepath.android.booksearch.net.BookClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList


class BookListActivity : AppCompatActivity() {
    private lateinit var rvBooks: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private var booksList: ArrayList<Book> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_book_list)
        rvBooks = findViewById(id.rvBooks)

        // Checkpoint #3
        // Switch Activity to Use a Toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar);
        // see http://guides.codepath.org/android/Using-the-App-ToolBar#using-toolbar-as-actionbar

        // Initialize the adapter
        bookAdapter = BookAdapter(this, booksList)
        bookAdapter.setOnItemClickListener (object : OnItemClickListener {
            override fun onItemClick(itemView: View?, position: Int) {
                Toast.makeText(
                    this@BookListActivity,
                    "An item at position $position clicked!",
                    Toast.LENGTH_SHORT).show()

                // Handle item click here:
                // Checkpoint #5
                // Hook up Book Detail View
                // see https://guides.codepath.org/android/Using-the-RecyclerView#attaching-click-handlers-using-listeners for setting up click listeners

                // Create Intent to start BookDetailActivity
                // Get Book at the given position
                // Pass the book into details activity using extras
                // see http://guides.codepath.org/android/Using-Intents-to-Create-Flows
                // see kotlin-parcelize https://developer.android.com/kotlin/parcelize plugin to do this
            }
        })
        // Attach the adapter to the RecyclerView
        rvBooks.adapter = bookAdapter

        // Set layout manager to position the items
        rvBooks.layoutManager = LinearLayoutManager(this)

        // Fetch the data remotely
        fetchBooks("Oscar Wilde")
    }

    // Executes an API call to the OpenLibrary search endpoint, parses the results
    // Converts them into an array of book objects and adds them to the adapter
    private fun fetchBooks(query: String) {
        val client = BookClient()
        client.getBooks(query, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, response: JSON) {
                try {
                    // Get the docs json array
                    val docs: JSONArray = response.jsonObject.getJSONArray("docs")
                    // Parse json array into array of model objects
                    val books = Book.fromJson(docs)
                    // Remove all books from the adapter
                    booksList.clear()
                    // Load model objects into the adapter
                    for (book in books) {
                        booksList.add(book) // add book through the adapter
                    }
                    bookAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    // Invalid JSON format, show appropriate error.
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers, responseString: String, throwable: Throwable) {
                // Handle failed request here
                Log.e(BookListActivity::class.java.simpleName,
                    "Request failed with code $statusCode. Response message: $responseString")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_book_list, menu)
        // Checkpoint #4
        // Add SearchView to Toolbar
        // Refer to http://guides.codepath.org/android/Extended-ActionBar-Guide#adding-searchview-to-actionbar guide for more details


        // Checkpoint #7 Show Progress Bar
        // see https://guides.codepath.org/android/Handling-ProgressBars#progress-within-actionbar

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }
}