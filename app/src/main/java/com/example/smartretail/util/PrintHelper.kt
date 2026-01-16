package com.example.smartretail.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.smartretail.data.local.OrderDetailItem
import com.example.smartretail.data.local.Transaction
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Helper class untuk print receipt (struk transaksi)
 * SRS Use Case: Cetak Struk (extend dari Transaksi Penjualan)
 */
object PrintHelper {
    
    /**
     * Print receipt untuk transaksi
     * @param context Android context
     * @param transaction Data transaksi
     * @param items List item dengan detail produk (dari JOIN query)
     * @param storeName Nama toko
     */
    fun printReceipt(
        context: Context,
        transaction: Transaction,
        items: List<OrderDetailItem>,
        storeName: String
    ) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        
        val jobName = "Receipt_${transaction.transactionId.take(8)}"
        
        val printAdapter = ReceiptPrintAdapter(
            context,
            transaction,
            items,
            storeName
        )
        
        printManager.print(jobName, printAdapter, null)
    }
}

/**
 * Custom PrintDocumentAdapter untuk generate PDF receipt
 */
class ReceiptPrintAdapter(
    private val context: Context,
    private val transaction: Transaction,
    private val items: List<OrderDetailItem>,
    private val storeName: String
) : PrintDocumentAdapter() {
    
    private var pdfDocument: PdfDocument? = null
    
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }
        
        // Create PDF document
        pdfDocument = PdfDocument()
        
        val info = PrintDocumentInfo.Builder("receipt.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()
        
        callback?.onLayoutFinished(info, true)
    }
    
    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onWriteCancelled()
            pdfDocument?.close()
            return
        }
        
        try {
            // Create page (thermal receipt size: 80mm width)
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument?.startPage(pageInfo)
            
            page?.let {
                val canvas = it.canvas
                val paint = android.graphics.Paint()
                
                var yPos = 50f
                val leftMargin = 50f
                
                // Header - Store Name
                paint.textSize = 24f
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                paint.textAlign = android.graphics.Paint.Align.CENTER
                canvas.drawText(storeName, 297.5f, yPos, paint)
                yPos += 40f
                
                // Transaction ID
                paint.textSize = 12f
                paint.typeface = android.graphics.Typeface.DEFAULT
                canvas.drawText("Order #${transaction.transactionId.take(8)}", 297.5f, yPos, paint)
                yPos += 30f
                
                // Date
                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                canvas.drawText(dateFormat.format(transaction.date), 297.5f, yPos, paint)
                yPos += 40f
                
                // Separator line
                canvas.drawLine(leftMargin, yPos, 545f, yPos, paint)
                yPos += 30f
                
                // Items header
                paint.textAlign = android.graphics.Paint.Align.LEFT
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                canvas.drawText("Item", leftMargin, yPos, paint)
                canvas.drawText("Qty", 300f, yPos, paint)
                canvas.drawText("Subtotal", 400f, yPos, paint)
                yPos += 25f
                
                // Separator
                canvas.drawLine(leftMargin, yPos, 545f, yPos, paint)
                yPos += 25f
                
                // Items
                paint.typeface = android.graphics.Typeface.DEFAULT
                val rupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                
                items.forEach { item ->
                    // Product name (truncate if too long)
                    val productName = if (item.name.length > 25) {
                        item.name.take(22) + "..."
                    } else {
                        item.name
                    }
                    canvas.drawText(productName, leftMargin, yPos, paint)
                    canvas.drawText("${item.qty}x", 300f, yPos, paint)
                    canvas.drawText(rupiah.format(item.subtotal), 400f, yPos, paint)
                    yPos += 25f
                }
                
                yPos += 10f
                // Separator
                canvas.drawLine(leftMargin, yPos, 545f, yPos, paint)
                yPos += 30f
                
                // Total
                paint.textSize = 16f
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                canvas.drawText("TOTAL:", leftMargin, yPos, paint)
                canvas.drawText(rupiah.format(transaction.totalPrice), 400f, yPos, paint)
                yPos += 50f
                
                // Footer
                paint.textSize = 10f
                paint.typeface = android.graphics.Typeface.DEFAULT
                paint.textAlign = android.graphics.Paint.Align.CENTER
                canvas.drawText("Terima kasih atas kunjungan Anda!", 297.5f, yPos, paint)
                yPos += 20f
                canvas.drawText("Powered by SmartRetail", 297.5f, yPos, paint)
                
                pdfDocument?.finishPage(it)
            }
            
            // Write to file
            pdfDocument?.writeTo(FileOutputStream(destination?.fileDescriptor))
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        } finally {
            pdfDocument?.close()
        }
    }
}
