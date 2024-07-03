package io.github.gouthams22.fetchrewardscodingexercise.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import io.github.gouthams22.fetchrewardscodingexercise.R
import io.github.gouthams22.fetchrewardscodingexercise.adapter.ItemListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    /**
     * Data class to hold the item data
     * @param id The id of the item
     * @param listId The id of the list
     * @param name The name of the item
     */
    @Serializable
    data class Item(val id: Int, val listId: Int, val name: String)

    /**
     * Data class to hold the list data
     * @param listId The id of the list
     * @param items The list of items
     */
    class ItemList(val listId: Int, val items: ArrayList<Item> = ArrayList()) {
        fun add(item: Item) {
            items += item
        }
    }

    //Contains all the items fetched from dataUrl
    private var itemList: ArrayList<Item> = ArrayList()

    //Contains all the items to be displayed filtered from itemList
    private var displayList: ArrayList<ItemList> = ArrayList()

    // Given url for the assessment
    private val dataUrl: String = "https://fetch-hiring.s3.amazonaws.com/hiring.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Fetch data from url
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                //Fetch data from url and parse it to JsonArray
                val jsonArray = JSONArray(fetchData(dataUrl))
                for (i in 0 until jsonArray.length()) {
                    //Get each item from jsonArray, convert it into JsonObject, then to Item and add it to itemList
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getInt("id")
                    val listId = jsonObject.getInt("listId")
                    val name = jsonObject.getString("name")
                    val item = Item(id, listId, name)
                    itemList += item
                }
                //Filter out items with empty name
                itemList = itemList.filter { item ->
                    !(item.name.isEmpty() || item.name.isBlank() || item.name.contains("null"))
                } as ArrayList<Item>
                //Sort by listId and name
                itemList =
                    ArrayList<Item>(itemList.sortedWith(compareBy<Item> { it.listId }.thenBy { it.name }))
                //Group by listId
                for (i in itemList) {
                    if (displayList.size == 0 || displayList[displayList.size - 1].listId != i.listId) {
                        displayList.add(ItemList(i.listId))
                    }
                    displayList[displayList.size - 1].add(i)
                }
                Log.d(TAG, "itemList: $itemList")
            }
            // inflate recyclerview and hide progress indicator
            findViewById<LinearProgressIndicator>(R.id.loading_indicator).visibility =
                View.INVISIBLE
            val recyclerView: RecyclerView = findViewById(R.id.recycler_item_list)
            recyclerView.apply {
                adapter = ItemListAdapter(displayList)
                layoutManager = LinearLayoutManager(this@MainActivity)
                setHasFixedSize(true)
            }
        }
    }

    /**
     * Fetches the data from the given url
     * @param url The url to fetch the data from
     * @return The data fetched from the url as a string
     */
    private fun fetchData(url: String): String {
        val urlActual = URL(url)
        val connection = urlActual.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return connection.inputStream.bufferedReader().use { it.readText() }
    }
}