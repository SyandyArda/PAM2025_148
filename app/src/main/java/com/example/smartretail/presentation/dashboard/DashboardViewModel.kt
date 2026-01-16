package com.example.smartretail.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartretail.data.local.BestSellingProduct
import com.example.smartretail.data.local.DailyRevenue
import com.example.smartretail.domain.repository.ProductRepository
import com.example.smartretail.domain.repository.TransactionRepository
import com.example.smartretail.ui.components.Period
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DashboardState(
    val totalRevenue: Long = 0,
    val totalTransaction: Int = 0,
    val totalProduct: Int = 0,
    val lowStockCount: Int = 0,  // SRS Section 2.2 - Notifikasi Stok
    // Analytics (SRS Section 2.2 - Laporan Penjualan)
    val todayRevenue: Long = 0,
    val todayTransactionCount: Int = 0,
    val bestSellingProducts: List<BestSellingProduct> = emptyList(),
    // Revenue Trends for Chart
    val revenueData: List<DailyRevenue> = emptyList(),
    val selectedPeriod: Period = Period.WEEKLY
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    productRepository: ProductRepository
) : ViewModel() {

    // Helper function untuk mendapatkan range waktu hari ini
    private fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        
        // Set ke awal hari ini (00:00:00.000)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        // Set ke akhir hari ini (23:59:59.999)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis
        
        // Debug logging
        android.util.Log.d("DashboardVM", "Today range: ${java.util.Date(startOfDay)} to ${java.util.Date(endOfDay)}")
        
        return Pair(startOfDay, endOfDay)
    }

    private val todayRange = getTodayRange()

    // Magic: Menggabungkan 7 aliran data (Flow) menjadi satu State UI
    val uiState: StateFlow<DashboardState> = combine(
        transactionRepository.getTotalRevenue(),
        transactionRepository.getTransactionCount(),
        productRepository.getProductCount(),
        productRepository.getLowStockCount(10),  // Threshold = 10
        transactionRepository.getTodayRevenue(todayRange.first, todayRange.second),
        transactionRepository.getTodayTransactionCount(todayRange.first, todayRange.second),
        transactionRepository.getBestSellingProducts()
    ) { values ->
        DashboardState(
            totalRevenue = values[0] as Long,
            totalTransaction = values[1] as Int,
            totalProduct = values[2] as Int,
            lowStockCount = values[3] as Int,
            todayRevenue = values[4] as Long,
            todayTransactionCount = values[5] as Int,
            bestSellingProducts = values[6] as List<BestSellingProduct>
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )
    
    // Load revenue data based on selected period
    private val _revenueData = MutableStateFlow<List<DailyRevenue>>(emptyList())
    val revenueData = _revenueData.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow(Period.WEEKLY)
    val selectedPeriod = _selectedPeriod.asStateFlow()
    
    init {
        loadRevenueData(Period.WEEKLY)
    }
    
    fun selectPeriod(period: Period) {
        _selectedPeriod.value = period
        loadRevenueData(period)
    }
    
    private fun loadRevenueData(period: Period) {
        viewModelScope.launch {
            val data = when (period) {
                Period.DAILY -> transactionRepository.getWeeklyRevenue().takeLast(1)
                Period.WEEKLY -> transactionRepository.getWeeklyRevenue()
                Period.MONTHLY -> transactionRepository.getMonthlyRevenue()
            }
            _revenueData.value = data
        }
    }
}