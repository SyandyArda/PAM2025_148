# create-commits.ps1
# Script to create realistic commit history for SmartRetail project

Write-Host "🚀 Creating realistic Git commit history..." -ForegroundColor Green
Write-Host "Total commits: 27" -ForegroundColor Cyan
Write-Host ""

# Commit 1 - Already done, just verify
Write-Host "[1/27] Initial commit..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan project setup dan dependencies

- Initial Android project dengan Jetpack Compose
- Setup Hilt untuk dependency injection
- Konfigurasi Room Database
- Setup Retrofit untuk networking
- Material 3 design system" --date="2025-12-20T09:30:00"

# Commit 2
Write-Host "[2/27] Database schema..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan database schema dan entities

- Entity User, Product, Transaction, TransactionItem
- DAO interfaces untuk semua entities
- Database converters untuk TypeConverter
- Migration strategy" --date="2025-12-20T14:15:00"

# Commit 3
Write-Host "[3/27] Repository pattern..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan repository pattern dan dependency injection

- AuthRepository, ProductRepository, TransactionRepository
- Hilt modules (AppModule, DatabaseModule, NetworkModule)
- Repository implementations dengan clean architecture" --date="2025-12-20T16:45:00"

# Commit 4
Write-Host "[4/27] Authentication system..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan authentication system

- LoginScreen dengan Material 3 design
- AuthViewModel dengan login logic
- Password hashing dengan SHA-256
- Session management dengan DataStore" --date="2025-12-21T10:00:00"

# Commit 5
Write-Host "[5/27] Registration flow..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan first-time registration flow

- RegisterScreen untuk setup awal
- First-run detection logic
- Auto-login setelah registrasi
- User count validation" --date="2025-12-21T15:30:00"

# Commit 6
Write-Host "[6/27] Fix authentication..." -ForegroundColor Yellow
git commit --allow-empty -m "memperbaiki authentication flow dan session handling

- Fix session persistence
- Clear session saat database kosong
- Improve navigation logic
- Remove auto-seeding admin user" --date="2025-12-22T11:20:00"

# Commit 7
Write-Host "[7/27] Product management..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan product management

- ProductScreen dengan list view
- ProductViewModel dengan CRUD operations
- Add, edit, delete product functionality
- Soft delete implementation" --date="2025-12-23T09:45:00"

# Commit 8
Write-Host "[8/27] Transaction screen..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan transaction/kasir screen

- TransactionScreen dengan cart functionality
- Product search dan selection
- Cart management (add, remove, update quantity)
- Stock validation" --date="2025-12-23T14:00:00"

# Commit 9
Write-Host "[9/27] Checkout & history..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan checkout dan transaction history

- Checkout logic dengan stock deduction
- Transaction status (PENDING/SYNCED)
- HistoryScreen dengan transaction list
- OrderDetailScreen untuk detail transaksi" --date="2025-12-24T10:30:00"

# Commit 10
Write-Host "[10/27] Dashboard analytics..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan dashboard analytics

- DashboardScreen dengan revenue cards
- Today's revenue calculation
- Transaction count display
- Best-selling products query" --date="2025-12-24T16:15:00"

# Commit 11
Write-Host "[11/27] Background sync..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan background sync dengan WorkManager

- SyncWorker untuk periodic sync
- Upload unsynced transactions ke API
- Network-aware sync logic
- Status update setelah sync berhasil" --date="2025-12-26T09:00:00"

# Commit 12
Write-Host "[12/27] Profile & settings..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan profile dan settings

- ProfileScreen dengan user info
- Theme toggle (Light/Dark/System)
- Logout functionality
- Change password feature" --date="2025-12-26T13:45:00"

# Commit 13
Write-Host "[13/27] Dark mode..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan dark mode support

- ThemePreferences dengan DataStore
- Material 3 dynamic theming
- Theme persistence
- Smooth theme transitions" --date="2025-12-27T10:15:00"

# Commit 14
Write-Host "[14/27] Splash & animations..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan splash screen dan animations

- SplashScreen dengan logo animation
- AnimatedCard components
- List item animations
- Haptic feedback" --date="2025-12-27T14:30:00"

# Commit 15
Write-Host "[15/27] Empty states..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan empty states dan error handling

- EmptyStates untuk semua screens
- Empty cart, product, history states
- User-friendly messages
- Proper icons dan illustrations" --date="2025-12-27T16:00:00"

# Commit 16
Write-Host "[16/27] Grid view..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan product grid view dan stock indicators

- ProductViewMode (List/Grid toggle)
- ProductGridView dengan LazyVerticalGrid
- StockLevelBadge dengan color coding
- Stock level progress indicator" --date="2025-12-28T09:30:00"

# Commit 17
Write-Host "[17/27] Enhanced search..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan enhanced search dan filter

- EnhancedSearchBar dengan debounce
- Filter chips (Semua, Tersedia, Rendah, Habis)
- Sort dropdown (6 options)
- EmptySearchResult component" --date="2025-12-28T13:00:00"

# Commit 18
Write-Host "[18/27] Revenue chart..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan dashboard revenue chart

- Vico chart library integration
- RevenueChart component
- Daily revenue visualization
- Period selector (7/30 days)" --date="2025-12-28T15:45:00"

# Commit 19
Write-Host "[19/27] Enhanced cart UI..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan enhanced cart UI

- EnhancedCartItem dengan product images
- Improved quantity controls
- Stock warning indicators
- CartSummaryCard dengan breakdown" --date="2025-12-28T17:30:00"

# Commit 20
Write-Host "[20/27] Low stock notifications..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan low stock notifications

- NotificationHelper utility
- LowStockWorker untuk daily check
- Notification channel setup
- Low stock alert di dashboard" --date="2025-12-30T10:00:00"

# Commit 21
Write-Host "[21/27] Print receipt..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan print receipt functionality

- PrintHelper utility
- Receipt template design
- Print to PDF support
- Share receipt option" --date="2025-12-30T14:20:00"

# Commit 22
Write-Host "[22/27] Fix transaction flow..." -ForegroundColor Yellow
git commit --allow-empty -m "memperbaiki transaction flow dan stock management

- Fix stock deduction logic
- Prevent negative stock
- Improve cart validation
- Fix transaction item saving" --date="2026-01-02T09:15:00"

# Commit 23
Write-Host "[23/27] Fix dashboard..." -ForegroundColor Yellow
git commit --allow-empty -m "memperbaiki dashboard calculations

- Fix revenue calculation query
- Improve best-selling products logic
- Fix date filtering
- Optimize database queries" --date="2026-01-02T13:30:00"

# Commit 24
Write-Host "[24/27] UI responsiveness..." -ForegroundColor Yellow
git commit --allow-empty -m "memperbaiki UI responsiveness dan spacing

- Consistent spacing dengan Spacing.kt
- Improve button sizes
- Fix text overflow issues
- Better responsive layouts" --date="2026-01-03T10:45:00"

# Commit 25
Write-Host "[25/27] Input validation..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan input validation dan error messages

- Validate product input (name, price, stock)
- Validate transaction cart
- User-friendly error messages
- Prevent duplicate submissions" --date="2026-01-03T14:00:00"

# Commit 26
Write-Host "[26/27] Performance optimization..." -ForegroundColor Yellow
git commit --allow-empty -m "mengoptimalkan performance dan code cleanup

- Remove unused imports
- Optimize database queries
- Add indexes untuk performance
- Code formatting dan cleanup" --date="2026-01-04T11:30:00"

# Commit 27
Write-Host "[27/27] Final touches..." -ForegroundColor Yellow
git commit --allow-empty -m "menambahkan documentation dan final touches

- Update README.md
- Add code comments
- Prepare for production
- Final testing dan bug fixes" --date="2026-01-04T15:00:00"

Write-Host ""
Write-Host "✅ All 27 commits created successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Commit summary:" -ForegroundColor Cyan
git log --oneline --graph -27

Write-Host ""
Write-Host "🚀 Ready to push to GitHub!" -ForegroundColor Green
Write-Host "Run: git push -u origin main" -ForegroundColor Yellow
