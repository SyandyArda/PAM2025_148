package com.example.smartretail.presentation.product

/**
 * View mode for product display
 */
enum class ProductViewMode {
    LIST,
    GRID
}

/**
 * Stock filter options
 */
enum class StockFilter {
    ALL,
    OUT_OF_STOCK,
    LOW_STOCK,
    AVAILABLE
}

/**
 * Sort options for products
 */
enum class SortBy {
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    STOCK_ASC,
    STOCK_DESC
}
