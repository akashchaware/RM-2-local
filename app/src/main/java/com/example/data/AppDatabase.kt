package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Entity(tableName = "repair_requests")
data class RepairRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val code: String,
    val deviceBrand: String,
    val deviceModel: String,
    val issueDescription: String,
    val city: String,
    val customerName: String,
    val customerPhone: String,
    val statusStep: Int, // 1 to 11
    val technicianId: Long? = null,
    val technicianName: String? = null,
    val repairMasterId: Long? = null,
    val serviceChargePercent: Int = 15,
    val quotePartsString: String = "", // Format: "Screen,120.0,1;Battery,50.0,1"
    val quoteServiceCharge: Double = 0.0,
    val totalAmount: Double = 0.0,
    val isApprovedByCustomer: Boolean = false,
    val pickupOtpCode: String = "4321",
    val deliveryOtpCode: String = "8765",
    val isPickupOtpVerified: Boolean = false,
    val isDeliveryOtpVerified: Boolean = false,
    val isMaintenanceModeEnabled: Boolean = false,
    val isDeviceDeadOrBlank: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Helper to get step title and customer action
    fun getStepTitle(): String {
        return when (statusStep) {
            1 -> "Request Placed"
            2 -> "Technician Assigned"
            3 -> "Doorstep Pickup Initiated"
            4 -> "Device Received at Workshop"
            5 -> "Diagnosis Underway"
            6 -> "Quotation Awaiting Customer Approval"
            7 -> "Quotation Approved - Repairing"
            8 -> "Repair Completed & Quality Check Done"
            9 -> "Out for Delivery"
            10 -> "Delivery OTP Verification"
            11 -> "Completed & Handed Over"
            else -> "Processing"
        }
    }
}

@Entity(tableName = "parts")
data class Part(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val price: Double,
    val category: String
)

@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val grade: String, // Mint, Excellent, Good
    val storage: String, // 64GB, 128GB, 256GB, 512GB
    val description: String,
    val price: Double,
    val city: String,
    val imageUrl: String,
    val isSold: Boolean = false,
    val sellerName: String = "RepairMaster Official",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val listingId: Long,
    val listingTitle: String,
    val buyerName: String,
    val buyerPhone: String,
    val buyerAddress: String,
    val amountPaid: Double,
    val status: String = "Paid", // Paid, Shipped, Delivered
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String = "current_user",
    val role: String = "CUSTOMER", // CUSTOMER, TECHNICIAN, REPAIRMASTER, COORDINATOR, ADMIN, MARKETPLACE_BUYER
    val name: String = "Rajesh Deshmukh",
    val phone: String = "+91 98230 12345",
    val email: String = "rajesh.deshmukh@gmail.com",
    val city: String = "Nagpur",
    val password: String = "12345",
    val isTechnicianApplied: Boolean = false,
    val isRepairMasterApplied: Boolean = false,
    val technicianStatus: String = "None", // None, Pending, Approved, Rejected
    val repairMasterStatus: String = "None" // None, Pending, Approved, Rejected
)

@Entity(tableName = "job_postings")
data class JobPosting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val roleType: String, // Technician, Repair Master
    val location: String,
    val description: String,
    val isClosed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val repairRequestId: Long,
    val customerName: String,
    val technicianId: Long?,
    val technicianName: String?,
    val repairMasterId: Long? = 101L,
    val repairMasterName: String? = "Pranay Pathak",
    val techRating: Int, // 1-5 stars
    val masterRating: Int, // 1-5 stars
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "promotions")
data class Promotion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String,
    val offerPrice: Double,
    val isCustomImage: Boolean = false,
    val customImageBytesHex: String = "", // Hex-encoded image bytes if coordinator uploaded
    val drawableResName: String = "img_dead_phone_offer_1782763644093",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface AppDao {
    // Promotions
    @Query("SELECT * FROM promotions ORDER BY timestamp DESC")
    fun getAllPromotions(): Flow<List<Promotion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotion(promotion: Promotion): Long

    @Query("DELETE FROM promotions WHERE id = :id")
    suspend fun deletePromotion(id: Long)

    // Repair Requests
    @Query("SELECT * FROM repair_requests ORDER BY timestamp DESC")
    fun getAllRepairRequests(): Flow<List<RepairRequest>>

    @Query("SELECT * FROM repair_requests WHERE id = :id")
    fun getRepairRequestById(id: Long): Flow<RepairRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairRequest(request: RepairRequest): Long

    @Update
    suspend fun updateRepairRequest(request: RepairRequest)

    @Query("DELETE FROM repair_requests WHERE id = :id")
    suspend fun deleteRepairRequest(id: Long)

    // Parts
    @Query("SELECT * FROM parts ORDER BY name ASC")
    fun getAllParts(): Flow<List<Part>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: Part): Long

    @Query("DELETE FROM parts WHERE id = :id")
    suspend fun deletePart(id: Long)

    // Listings
    @Query("SELECT * FROM listings ORDER BY timestamp DESC")
    fun getAllListings(): Flow<List<Listing>>

    @Query("SELECT * FROM listings WHERE id = :id")
    fun getListingById(id: Long): Flow<Listing?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: Listing): Long

    @Update
    suspend fun updateListing(listing: Listing)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListing(id: Long)

    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 'current_user'")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles")
    fun getAllUserProfilesFlow(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles")
    suspend fun getAllUserProfiles(): List<UserProfile>

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    suspend fun getUserProfileById(id: String): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE phone = :phone LIMIT 1")
    suspend fun getUserProfileByPhone(phone: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Job Postings
    @Query("SELECT * FROM job_postings ORDER BY timestamp DESC")
    fun getAllJobPostings(): Flow<List<JobPosting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobPosting(posting: JobPosting): Long

    @Update
    suspend fun updateJobPosting(posting: JobPosting)

    @Query("DELETE FROM job_postings WHERE id = :id")
    suspend fun deleteJobPosting(id: Long)

    // Reviews
    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    // Bulk deletion queries for launch preparation
    @Query("DELETE FROM repair_requests")
    suspend fun clearRepairRequests()

    @Query("DELETE FROM listings")
    suspend fun clearListings()

    @Query("DELETE FROM reviews")
    suspend fun clearReviews()

    @Query("DELETE FROM job_postings")
    suspend fun clearJobPostings()

    @Query("DELETE FROM promotions")
    suspend fun clearPromotions()

    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}

@Database(
    entities = [
        RepairRequest::class,
        Part::class,
        Listing::class,
        Order::class,
        UserProfile::class,
        JobPosting::class,
        Review::class,
        Promotion::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "repairing_master_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun populateInitialData(db: AppDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = db.appDao()

                // Check if profile exists, if not create default
                dao.getUserProfile().collect { profile ->
                    if (profile == null) {
                        dao.insertUserProfile(UserProfile())
                    }
                }
            }
        }
    }
}
