package hr.foi.air.mshop.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object MockProducts {
    private val _products = MutableStateFlow(listOf(
        Product(1, "Laptop Pro 15", "Moćan laptop s Intel i7, 16GB RAM", 1299.99),
        Product(2, "Bežični Miš X", "Ergonomski bežični miš s dugom baterijom", 25.50),
        Product(3, "Mehanička Tipkovnica K7", "RGB mehanička tipkovnica sa taktilnim prekidačima", 89.90),
        Product(4, "4K Monitor 27-inčni", "27\" 4K IPS monitor s HDR podrškom", 349.00),
        Product(5, "USB-C Hub 8-u-1", "Hub s više priključaka: HDMI, USB, Ethernet", 45.00),
        Product(6, "Gaming Slušalice G-Pro", "Slušalice s surround zvukom i mikrofonom", 119.99),
        Product(7, "Prijenosni SSD 1TB", "Brzi NVMe SSD za prijenos podataka", 99.99)
    ))

    val allProducts = _products.asStateFlow()

    fun deleteProduct(productId: Int) {
        _products.update { currentList ->
            currentList.filterNot { it.id == productId }
        }
    }
}