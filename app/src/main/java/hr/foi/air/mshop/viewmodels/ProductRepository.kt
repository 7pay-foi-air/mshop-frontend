package hr.foi.air.mshop.viewmodels

import kotlinx.coroutines.flow.StateFlow

interface ProductRepository {
    fun getAllProducts(): StateFlow<List<Product>>
    fun deleteProduct(productId: Int)
}

class MockProductRepository : ProductRepository {
    override fun getAllProducts(): StateFlow<List<Product>> {
        return MockProducts.allProducts
    }

    override fun deleteProduct(productId: Int) {
        MockProducts.deleteProduct(productId)
    }
}