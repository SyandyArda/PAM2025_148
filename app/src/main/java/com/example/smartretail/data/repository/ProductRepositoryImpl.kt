package com.example.smartretail.data.repository

import com.example.smartretail.data.local.Product
import com.example.smartretail.data.local.ProductDao
import com.example.smartretail.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// @Inject constructor artinya: "Hilt, tolong carikan ProductDao, lalu masukkan ke sini"
class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> {
        return dao.getAllProducts()
    }

    override suspend fun insertProduct(name: String, price: Long, stock: Int) {
        val newProduct = Product(
            name = name,
            price = price,
            stock = stock,
            isSynced = false // Default false karena belum upload ke server
        )
        dao.insertProduct(newProduct)
    }

    override suspend fun deleteProduct(productId: Int) {
        // Ingat konsep Soft Delete di SRS? Kita tidak hapus barisnya, cuma tandai 'deleted'
        dao.softDeleteProduct(productId)
    }

    override suspend fun updateProduct(product: Product) {
        dao.updateProduct(product)
    }

    override fun getProductCount(): kotlinx.coroutines.flow.Flow<Int> {
        return dao.getProductCount()
    }

    override fun getLowStockProducts(threshold: Int): Flow<List<Product>> {
        return dao.getLowStockProducts(threshold)
    }

    override fun getLowStockCount(threshold: Int): Flow<Int> {
        return dao.getLowStockCount(threshold)
    }

}