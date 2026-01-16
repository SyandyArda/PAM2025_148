package com.example.smartretail.presentation.product

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartretail.data.local.Product
import com.example.smartretail.ui.components.EmptyProductState
import com.example.smartretail.ui.components.ProductGridView
import com.example.smartretail.ui.components.StockLevelBadge
import com.example.smartretail.ui.components.StockLevelIndicator
import com.example.smartretail.ui.components.EnhancedSearchBar
import com.example.smartretail.ui.components.EmptySearchResult
import com.example.smartretail.util.HapticHelper
import com.example.smartretail.util.HapticType
import com.example.smartretail.util.rememberHaptic
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    val productList by viewModel.productList.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val haptic = rememberHaptic()
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var productToEditId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var viewMode by remember { mutableStateOf(ProductViewMode.LIST) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(StockFilter.ALL) }
    var selectedSort by remember { mutableStateOf(SortBy.NAME_ASC) }
    
    // Apply search, filter, and sort
    val filteredProducts = remember(productList, searchQuery, selectedFilter, selectedSort) {
        productList
            .filter { it.name.contains(searchQuery, ignoreCase = true) }
            .filter { product ->
                when (selectedFilter) {
                    StockFilter.ALL -> true
                    StockFilter.OUT_OF_STOCK -> product.stock == 0
                    StockFilter.LOW_STOCK -> product.stock in 1..10
                    StockFilter.AVAILABLE -> product.stock > 10
                }
            }
            .let { list ->
                when (selectedSort) {
                    SortBy.NAME_ASC -> list.sortedBy { it.name }
                    SortBy.NAME_DESC -> list.sortedByDescending { it.name }
                    SortBy.PRICE_ASC -> list.sortedBy { it.price }
                    SortBy.PRICE_DESC -> list.sortedByDescending { it.price }
                    SortBy.STOCK_ASC -> list.sortedBy { it.stock }
                    SortBy.STOCK_DESC -> list.sortedByDescending { it.stock }
                }
            }
    }

    // Dialog Konfirmasi Edit
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Konfirmasi Edit") },
            text = { Text("Apakah Anda yakin ingin mengubah data produk ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (productToEditId != null) {
                            val cleanPrice = priceInput.replace(".", "")
                            viewModel.updateProduct(productToEditId!!, name, cleanPrice, stock)
                            Toast.makeText(context, "✓ Perubahan Disimpan!", Toast.LENGTH_SHORT).show()
                            isEditMode = false
                            productToEditId = null
                            name = ""
                            priceInput = ""
                            stock = ""
                        }
                        showEditDialog = false
                    }
                ) {
                    Text("SIMPAN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text("Hapus Produk?") },
            text = { Text("Yakin ingin menghapus '${productToDelete?.name}'? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { viewModel.deleteProduct(it.productId) }
                        showDeleteDialog = false
                        Toast.makeText(context, "✓ Produk dihapus", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("HAPUS")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header dengan gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF9800),
                            Color(0xFFFFB74D)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isEditMode) Icons.Default.Edit else Icons.Default.Inventory,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isEditMode) "Edit Produk" else "Kelola Produk",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${productList.size} produk terdaftar",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // View mode toggle button
                IconButton(
                    onClick = {
                        haptic(HapticType.CLICK)
                        viewMode = if (viewMode == ProductViewMode.LIST) {
                            ProductViewMode.GRID
                        } else {
                            ProductViewMode.LIST
                        }
                    }
                ) {
                    Icon(
                        if (viewMode == ProductViewMode.LIST) 
                            Icons.Default.GridView 
                        else 
                            Icons.Default.ViewList,
                        contentDescription = "Toggle View",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Form Input
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Barang") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { input ->
                            val cleanString = input.replace(Regex("[^\\d]"), "")
                            if (cleanString.isNotEmpty()) {
                                priceInput = formatRibuan(cleanString.toLong())
                            } else {
                                priceInput = ""
                            }
                        },
                        label = { Text("Harga") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        prefix = { Text("Rp ") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { if (it.all { char -> char.isDigit() }) stock = it },
                        label = { Text("Stok") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Inventory2, contentDescription = null) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isEditMode) {
                        OutlinedButton(
                            onClick = {
                                isEditMode = false
                                productToEditId = null
                                name = ""
                                priceInput = ""
                                stock = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Batal")
                        }
                    }

                    Button(
                        onClick = {
                            val cleanPrice = priceInput.replace(".", "")
                            if (name.isBlank() || cleanPrice.isBlank() || stock.isBlank()) {
                                Toast.makeText(context, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                            } else {
                                if (isEditMode) {
                                    showEditDialog = true
                                } else {
                                    viewModel.addProduct(name, cleanPrice, stock)
                                    Toast.makeText(context, "✓ Produk Ditambah!", Toast.LENGTH_SHORT).show()
                                    name = ""
                                    priceInput = ""
                                    stock = ""
                                }
                            }
                        },
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditMode) MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            if (isEditMode) Icons.Default.Save else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isEditMode) "SIMPAN PERUBAHAN" else "TAMBAH BARANG")
                    }
                }
            }
        }
        
        // Search Bar & Filters (Compact)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search Bar
            EnhancedSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* Already handled by state */ },
                placeholder = "Cari produk..."
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Filter Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == StockFilter.ALL,
                    onClick = { 
                        haptic(HapticType.CLICK)
                        selectedFilter = StockFilter.ALL 
                    },
                    label = { Text("Semua (${productList.size})", style = MaterialTheme.typography.bodySmall) }
                )
                FilterChip(
                    selected = selectedFilter == StockFilter.AVAILABLE,
                    onClick = { 
                        haptic(HapticType.CLICK)
                        selectedFilter = StockFilter.AVAILABLE 
                    },
                    label = { 
                        Text("Tersedia (${productList.count { it.stock > 10 }})", style = MaterialTheme.typography.bodySmall) 
                    }
                )
                FilterChip(
                    selected = selectedFilter == StockFilter.LOW_STOCK,
                    onClick = { 
                        haptic(HapticType.CLICK)
                        selectedFilter = StockFilter.LOW_STOCK 
                    },
                    label = { 
                        Text("Rendah (${productList.count { it.stock in 1..10 }})", style = MaterialTheme.typography.bodySmall) 
                    }
                )
            }
            
            Spacer(Modifier.height(6.dp))
            
            // Sort Dropdown (Compact)
            var expandedSort by remember { mutableStateOf(false) }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box {
                    OutlinedButton(
                        onClick = { expandedSort = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Sort, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            when (selectedSort) {
                                SortBy.NAME_ASC -> "Nama A-Z"
                                SortBy.NAME_DESC -> "Nama Z-A"
                                SortBy.PRICE_ASC -> "Harga ↑"
                                SortBy.PRICE_DESC -> "Harga ↓"
                                SortBy.STOCK_ASC -> "Stok ↑"
                                SortBy.STOCK_DESC -> "Stok ↓"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    DropdownMenu(
                        expanded = expandedSort,
                        onDismissRequest = { expandedSort = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nama A-Z") },
                            onClick = {
                                selectedSort = SortBy.NAME_ASC
                                expandedSort = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nama Z-A") },
                            onClick = {
                                selectedSort = SortBy.NAME_DESC
                                expandedSort = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Harga Terendah") },
                            onClick = {
                                selectedSort = SortBy.PRICE_ASC
                                expandedSort = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Harga Tertinggi") },
                            onClick = {
                                selectedSort = SortBy.PRICE_DESC
                                expandedSort = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Stok Terendah") },
                            onClick = {
                                selectedSort = SortBy.STOCK_ASC
                                expandedSort = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Stok Tertinggi") },
                            onClick = {
                                selectedSort = SortBy.STOCK_DESC
                                expandedSort = false
                            }
                        )
                    }
                }
            }
        }

        // List/Grid Barang dengan Empty State
        if (filteredProducts.isEmpty()) {
            if (searchQuery.isNotEmpty()) {
                EmptySearchResult(query = searchQuery)
            } else {
                EmptyProductState(
                    onAddClick = {
                        haptic(HapticType.CLICK)
                        // Scroll to top to show input form
                        scope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                        // User can now see the form and start typing
                    }
                )
            }
        } else {
            when (viewMode) {
                ProductViewMode.LIST -> {
                    LazyColumn(
                        state = scrollState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts, key = { it.productId }) { product ->
                            EnhancedProductItem(
                                product = product,
                                onEditClick = {
                                    haptic(HapticType.CLICK)
                                    isEditMode = true
                                    productToEditId = product.productId
                                    name = product.name
                                    priceInput = formatRibuan(product.price)
                                    stock = product.stock.toString()
                                    // Scroll to top to show form
                                    scope.launch {
                                        scrollState.animateScrollToItem(0)
                                    }
                                },
                                onDeleteClick = {
                                    haptic(HapticType.HEAVY_CLICK)
                                    productToDelete = product
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
                ProductViewMode.GRID -> {
                    ProductGridView(
                        products = filteredProducts,
                        onEditClick = { product ->
                            haptic(HapticType.CLICK)
                            isEditMode = true
                            productToEditId = product.productId
                            name = product.name
                            priceInput = formatRibuan(product.price)
                            stock = product.stock.toString()
                            // Scroll to top to show form
                            scope.launch {
                                scrollState.animateScrollToItem(0)
                            }
                        },
                        onDeleteClick = { product ->
                            haptic(HapticType.HEAVY_CLICK)
                            productToDelete = product
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun EnhancedProductItem(
    product: Product,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Produk
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFE0B2),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = toRupiah(product.price),
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                // Stock Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        product.stock == 0 -> Color(0xFFFFEBEE)
                        product.stock < 10 -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E9)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Inventory,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = when {
                                product.stock == 0 -> Color(0xFFC62828)
                                product.stock < 10 -> Color(0xFFEF6C00)
                                else -> Color(0xFF2E7D32)
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Stok: ${product.stock}",
                            fontSize = 12.sp,
                            color = when {
                                product.stock == 0 -> Color(0xFFC62828)
                                product.stock < 10 -> Color(0xFFEF6C00)
                                else -> Color(0xFF2E7D32)
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Action Buttons
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

fun toRupiah(amount: Long): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    return format.format(amount)
}

fun formatRibuan(number: Long): String {
    val localeID = Locale("in", "ID")
    val formatter = NumberFormat.getNumberInstance(localeID)
    return formatter.format(number)
}