package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository(private val appDao: AppDao) {

    val allRepairRequests: Flow<List<RepairRequest>> = appDao.getAllRepairRequests()
    val allParts: Flow<List<Part>> = appDao.getAllParts()
    val allListings: Flow<List<Listing>> = appDao.getAllListings()
    val allOrders: Flow<List<Order>> = appDao.getAllOrders()
    val userProfile: Flow<UserProfile?> = appDao.getUserProfile()
    val allUserProfiles: Flow<List<UserProfile>> = appDao.getAllUserProfilesFlow()

    suspend fun getUserProfileById(id: String): UserProfile? = appDao.getUserProfileById(id)
    suspend fun getUserProfileByPhone(phone: String): UserProfile? {
        val direct = appDao.getUserProfileByPhone(phone)
        if (direct != null) return direct

        // Robust last-10-digit matching fallback to support flexible inputs
        val normalizedInput = phone.filter { it.isDigit() }
        val last10Input = if (normalizedInput.length >= 10) normalizedInput.takeLast(10) else normalizedInput
        if (last10Input.isBlank()) return null

        val allProfiles = appDao.getAllUserProfiles()
        return allProfiles.find { profile ->
            val normalizedProfilePhone = profile.phone.filter { it.isDigit() }
            val last10Profile = if (normalizedProfilePhone.length >= 10) normalizedProfilePhone.takeLast(10) else normalizedProfilePhone
            last10Profile.isNotEmpty() && last10Input == last10Profile
        }
    }
    val allJobPostings: Flow<List<JobPosting>> = appDao.getAllJobPostings()
    val allReviews: Flow<List<Review>> = appDao.getAllReviews()
    val promotions: Flow<List<Promotion>> = appDao.getAllPromotions()

    fun getRepairRequestById(id: Long): Flow<RepairRequest?> = appDao.getRepairRequestById(id)
    fun getListingById(id: Long): Flow<Listing?> = appDao.getListingById(id)

    suspend fun insertPromotion(promotion: Promotion): Long = appDao.insertPromotion(promotion)
    suspend fun deletePromotion(id: Long) = appDao.deletePromotion(id)

    suspend fun insertReview(review: Review): Long = appDao.insertReview(review)

    suspend fun insertRepairRequest(request: RepairRequest): Long = appDao.insertRepairRequest(request)
    suspend fun updateRepairRequest(request: RepairRequest) = appDao.updateRepairRequest(request)
    suspend fun deleteRepairRequest(id: Long) = appDao.deleteRepairRequest(id)

    suspend fun insertPart(part: Part): Long = appDao.insertPart(part)
    suspend fun deletePart(id: Long) = appDao.deletePart(id)

    suspend fun insertListing(listing: Listing): Long = appDao.insertListing(listing)
    suspend fun updateListing(listing: Listing) = appDao.updateListing(listing)
    suspend fun deleteListing(id: Long) = appDao.deleteListing(id)

    suspend fun insertOrder(order: Order): Long = appDao.insertOrder(order)

    suspend fun updateUserProfile(profile: UserProfile) = appDao.insertUserProfile(profile)

    suspend fun insertJobPosting(posting: JobPosting): Long = appDao.insertJobPosting(posting)
    suspend fun updateJobPosting(posting: JobPosting) = appDao.updateJobPosting(posting)
    suspend fun deleteJobPosting(id: Long) = appDao.deleteJobPosting(id)

    // Prepopulate rich mock data so the app displays pre-loaded listings and repair tickets immediately
    fun prepopulateIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Prepopulate User Profile if empty
            val profile = appDao.getUserProfile().firstOrNull()
            if (profile == null) {
                val customer = UserProfile(
                    id = "+91 98230 12345",
                    role = "CUSTOMER",
                    name = "Rajesh Deshmukh",
                    phone = "+91 98230 12345",
                    email = "rajesh.deshmukh@gmail.com",
                    city = "Nagpur",
                    password = "12345"
                )
                val technician = UserProfile(
                    id = "+91 98230 55555",
                    role = "TECHNICIAN",
                    name = "Amit Sharma",
                    phone = "+91 98230 55555",
                    email = "amit.sharma@gmail.com",
                    city = "Nagpur",
                    password = "tech@rm2024"
                )
                val repairMaster = UserProfile(
                    id = "+91 98230 77777",
                    role = "REPAIRMASTER",
                    name = "Vinay Patel",
                    phone = "+91 98230 77777",
                    email = "vinay.patel@gmail.com",
                    city = "Nagpur",
                    password = "master@rm2024"
                )
                val admin = UserProfile(
                    id = "+91 95225 02707",
                    role = "ADMIN",
                    name = "Admin Owner",
                    phone = "+91 95225 02707",
                    email = "admin.owner@gmail.com",
                    city = "Nagpur",
                    password = "admin@rm2024"
                )

                // Insert into profiles
                appDao.insertUserProfile(customer)
                appDao.insertUserProfile(technician)
                appDao.insertUserProfile(repairMaster)
                appDao.insertUserProfile(admin)

                // Set default starting user as current_user
                appDao.insertUserProfile(customer.copy(id = "current_user"))
            }

            // 1b. Always ensure the main admin is present & up-to-date with correct password and role
            val mainAdminPhone = "+91 95225 02707"
            val existingAdmin = appDao.getUserProfileById(mainAdminPhone)
            if (existingAdmin == null) {
                appDao.insertUserProfile(
                    UserProfile(
                        id = mainAdminPhone,
                        role = "ADMIN",
                        name = "Admin Owner",
                        phone = mainAdminPhone,
                        email = "admin.owner@gmail.com",
                        city = "Nagpur",
                        password = "admin@rm2024"
                    )
                )
            } else {
                appDao.insertUserProfile(
                    existingAdmin.copy(
                        role = "ADMIN",
                        phone = mainAdminPhone,
                        password = "admin@rm2024"
                    )
                )
            }

            // 2. Prepopulate Parts if empty
            val partsList = appDao.getAllParts().firstOrNull()
            if (partsList.isNullOrEmpty()) {
                val initialParts = listOf(
                    Part(name = "OEM OLED Display (iPhone 13)", price = 11999.00, category = "Display"),
                    Part(name = "Premium OLED Panel (iPhone 12 Pro)", price = 9999.00, category = "Display"),
                    Part(name = "Original Super AMOLED (Galaxy S22)", price = 12499.00, category = "Display"),
                    Part(name = "OEM Battery (iPhone 11)", price = 2999.00, category = "Battery"),
                    Part(name = "Extended Capacity Battery (Galaxy S20)", price = 3499.00, category = "Battery"),
                    Part(name = "Primary Camera Sensor (Pixel 7 Pro)", price = 6999.00, category = "Camera"),
                    Part(name = "Front Self-Camera Module (iPhone 13)", price = 2499.00, category = "Camera"),
                    Part(name = "USB-C Charging Board (Pixel 6)", price = 1999.00, category = "Port"),
                    Part(name = "Lightning Charging Flex (iPhone X)", price = 1799.00, category = "Port"),
                    Part(name = "Tempered Front Glass Protector", price = 999.00, category = "Accessory")
                )
                initialParts.forEach { appDao.insertPart(it) }
            }

            // 3. Prepopulate Listings if empty
            val listingsList = appDao.getAllListings().firstOrNull()
            if (listingsList.isNullOrEmpty()) {
                val initialListings = listOf(
                    Listing(
                        title = "iPhone 13 Pro Max - Alpine Green",
                        grade = "Mint",
                        storage = "256GB",
                        description = "Flawless screen and housing. Battery health is at 94%. Checked and certified by authorized Nagpur repair masters.",
                        price = 54999.00,
                        city = "Nagpur",
                        imageUrl = "https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=500"
                    ),
                    Listing(
                        title = "Samsung Galaxy S22 Ultra 5G - Phantom Black",
                        grade = "Excellent",
                        storage = "128GB",
                        description = "Microscopic hair scratches on frame, screen is 100% pristine. Accessories and Bill-Box included.",
                        price = 42999.00,
                        city = "Nagpur",
                        imageUrl = "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500"
                    ),
                    Listing(
                        title = "Google Pixel 7 Pro - Hazel Gray",
                        grade = "Good",
                        storage = "128GB",
                        description = "Some noticeable cosmetic scuffs along metal frame. Completely tested and fully functional by Nagpur lab specialists. Great camera value.",
                        price = 28999.00,
                        city = "Nagpur",
                        imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=500"
                    )
                )
                initialListings.forEach { appDao.insertListing(it) }
            }

            // 4. Prepopulate Repair Requests if empty
            val requestsList = appDao.getAllRepairRequests().firstOrNull()
            if (requestsList.isNullOrEmpty()) {
                val initialRequests = listOf(
                    RepairRequest(
                        code = "RM-4382",
                        deviceBrand = "Apple",
                        deviceModel = "iPhone 14 Pro",
                        issueDescription = "Screen glass shattered near Sitabuldi Fort drop, touchscreen is partially unresponsive.",
                        city = "Nagpur",
                        customerName = "Amit Kulkarni",
                        customerPhone = "+91 98221 44556",
                        statusStep = 1, // Request Placed, unassigned
                        timestamp = System.currentTimeMillis() - 7200000 // 2 hours ago
                    ),
                    RepairRequest(
                        code = "RM-9218",
                        deviceBrand = "Samsung",
                        deviceModel = "Galaxy S22 Ultra",
                        issueDescription = "Battery discharges rapidly in hot weather. Swelling slightly observed in Dharampeth workshop.",
                        city = "Nagpur",
                        customerName = "Sneha Joshi",
                        customerPhone = "+91 97654 32109",
                        statusStep = 3, // Doorstep Pickup Initiated
                        technicianId = 101L,
                        technicianName = "Devendra Chaudhari",
                        isPickupOtpVerified = false,
                        pickupOtpCode = "4321",
                        timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
                    ),
                    RepairRequest(
                        code = "RM-1049",
                        deviceBrand = "Google",
                        deviceModel = "Pixel 6a",
                        issueDescription = "Device dropped in Ambazari Lake side puddle. Muffled speaker audio.",
                        city = "Nagpur",
                        customerName = "Sanjay Patil",
                        customerPhone = "+91 88888 77777",
                        statusStep = 6, // Quotation Awaiting Customer Approval
                        technicianId = 102L,
                        technicianName = "Rahul Deshmukh",
                        quotePartsString = "OEM Speaker Flex Assembly,2499.0,1;Water Damage Ultrasonic Bath,1999.0,1",
                        quoteServiceCharge = 1499.0,
                        serviceChargePercent = 18,
                        totalAmount = 5997.00,
                        isApprovedByCustomer = false,
                        timestamp = System.currentTimeMillis() - 172800000 // 2 days ago
                    ),
                    RepairRequest(
                        code = "RM-5421",
                        deviceBrand = "Apple",
                        deviceModel = "iPhone XR",
                        issueDescription = "Power button stuck, won't click anymore after Pune journey.",
                        city = "Pune",
                        customerName = "Rahul Chavan",
                        customerPhone = "+91 99999 88888",
                        statusStep = 11, // Completed & Handed Over
                        technicianId = 101L,
                        technicianName = "Devendra Chaudhari",
                        quotePartsString = "Volume/Power Ribbon Assembly,1999.0,1",
                        quoteServiceCharge = 999.00,
                        totalAmount = 2998.00,
                        isApprovedByCustomer = true,
                        isPickupOtpVerified = true,
                        isDeliveryOtpVerified = true,
                        timestamp = System.currentTimeMillis() - 345600000 // 4 days ago
                    )
                )
                initialRequests.forEach { appDao.insertRepairRequest(it) }
            }

            // 5. Prepopulate Job Postings if empty
            val jobPostingsList = appDao.getAllJobPostings().firstOrNull()
            if (jobPostingsList.isNullOrEmpty()) {
                val initialPostings = listOf(
                    JobPosting(
                        title = "Senior Mobile Device Diagnostics Technician",
                        roleType = "Technician",
                        location = "Nagpur, MH",
                        description = "Responsible for executing multi-point inspections on incoming client phones in Nagpur Dharampeth, performing advanced chip-level micro-soldering, and restoring water-damaged logic boards."
                    ),
                    JobPosting(
                        title = "Workshop Shop Owner / Repair Master Partner",
                        roleType = "Repair Master",
                        location = "Nagpur, MH",
                        description = "Affiliate your Nagpur retail brick-and-mortar storefront as an official RepairMaster diagnostic workshop. Receive high volume delivery repairs and stock official OEM replacement kits."
                    )
                )
                initialPostings.forEach { appDao.insertJobPosting(it) }
            }

            // 6. Prepopulate Reviews if empty
            val reviewsList = appDao.getAllReviews().firstOrNull()
            if (reviewsList.isNullOrEmpty()) {
                val initialReviews = listOf(
                    Review(
                        repairRequestId = 9991L,
                        customerName = "Amit Kulkarni",
                        technicianId = 101L,
                        technicianName = "Devendra Chaudhari",
                        repairMasterId = 101L,
                        repairMasterName = "Pranay Pathak",
                        techRating = 5,
                        masterRating = 5,
                        comment = "Outstanding doorstep pickup and extremely fast micro-soldering. The screen is as good as original! Highly recommended.",
                        timestamp = System.currentTimeMillis() - 172800000
                    ),
                    Review(
                        repairRequestId = 9992L,
                        customerName = "Sneha Joshi",
                        technicianId = 102L,
                        technicianName = "Rahul Deshmukh",
                        repairMasterId = 101L,
                        repairMasterName = "Pranay Pathak",
                        techRating = 5,
                        masterRating = 4,
                        comment = "Great diagnostics, they explained the board issue clearly and did ultrasonic cleaning. Slightly pricey but totally worth it.",
                        timestamp = System.currentTimeMillis() - 86400000
                    )
                )
                initialReviews.forEach { appDao.insertReview(it) }
            }

            // 5. Prepopulate Promotions if empty
            val promoList = appDao.getAllPromotions().firstOrNull()
            if (promoList.isNullOrEmpty()) {
                appDao.insertPromotion(
                    Promotion(
                        title = "Check Your Dead Phone",
                        description = "Get expert level motherboard diagnostics for completely dead phones in just ₹250!",
                        offerPrice = 250.0,
                        drawableResName = "img_dead_phone_offer_1782763644093"
                    )
                )
            }
        }
    }

    suspend fun clearAllTablesAndPrepareLaunch() {
        appDao.clearRepairRequests()
        appDao.clearListings()
        appDao.clearReviews()
        appDao.clearJobPostings()
        appDao.clearPromotions()
        appDao.clearOrders()
    }
}
