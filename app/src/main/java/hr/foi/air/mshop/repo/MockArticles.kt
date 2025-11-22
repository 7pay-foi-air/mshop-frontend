package hr.foi.air.mshop.repo

import hr.foi.air.mshop.core.models.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MockArticles {
    private val _articles = MutableStateFlow(
        listOf(
            Article(
                1,
                "123456",
                "Laptop Pro 15",
                "Moćan laptop s Intel i7, 16GB RAM",
                1299.99
            ),
            Article(
                2,
                "234567",
                "Bežični Miš X",
                "Ergonomski bežični miš s dugom baterijom",
                25.50
            ),
            Article(
                3,
                "345678",
                "Mehanička Tipkovnica K7",
                "RGB mehanička tipkovnica sa taktilnim prekidačima",
                89.90
            ),
            Article(
                4,
                "293847",
                "4K Monitor 27-inčni",
                "27\" 4K IPS monitor s HDR podrškom",
                349.00
            ),
            Article(
                5,
                "572967",
                "USB-C Hub 8-u-1",
                "Hub s više priključaka: HDMI, USB, Ethernet",
                45.00
            ),
            Article(
                6,
                "296551",
                "Gaming Slušalice G-Pro",
                "Slušalice s surround zvukom i mikrofonom",
                119.99
            ),
            Article(
                7,
                "965107",
                "Prijenosni SSD 1TB",
                "Brzi NVMe SSD za prijenos podataka",
                99.99
            )
        )
    )

    val allArticles = _articles.asStateFlow()

    fun deleteArticles(articleId: Int) {
        _articles.update { currentList ->
            currentList.filterNot { it.id == articleId }
        }
    }
}