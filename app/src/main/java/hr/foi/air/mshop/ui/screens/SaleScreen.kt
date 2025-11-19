package hr.foi.air.mshop.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hr.foi.air.mshop.ui.components.ArticleListItem
import hr.foi.air.mshop.ui.components.SearchField

data class Article(val id: Int, val name: String, val desc: String, val price: Double)
private val allArticles = listOf(
    Article(1, "Laptop Pro 15", "Moćan laptop s Intel i7, 16GB RAM", 1299.99),
    Article(2, "Bežični Miš X", "Ergonomski bežični miš s dugom baterijom", 25.50),
    Article(3, "Mehanička Tipkovnica K7", "RGB mehanička tipkovnica sa taktilnim prekidačima", 89.90),
    Article(4, "4K Monitor 27-inčni", "27\" 4K IPS monitor s HDR podrškom", 349.00),
    Article(5, "USB-C Hub 8-u-1", "Hub s više priključaka: HDMI, USB, Ethernet", 45.00),
    Article(6, "Gaming Slušalice G-Pro", "Slušalice s surround zvukom i mikrofonom", 119.99),
    Article(7, "Prijenosni SSD 1TB", "Brzi NVMe SSD za prijenos podataka", 99.99)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen() {
    var query by remember { mutableStateOf("") }

    val filteredArticles = if (query.isEmpty()) {
        allArticles
    } else {
        allArticles.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    Column( modifier = Modifier.fillMaxWidth()){
        SearchField(
            value = query,
            onValueChange = { query = it },
            placeholder = "Pretraži artikle...",
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredArticles) { article ->
                ArticleListItem(article = article)
            }
        }
    }
}