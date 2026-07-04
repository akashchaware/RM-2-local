package com.example.ui

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    Landing,
    CustomerDashboard,
    NewRepairRequest,
    RepairRequestDetail,
    TechnicianDashboard,
    CoordinatorDashboard,
    RepairMasterDashboard,
    AdminDashboard,
    Marketplace,
    MarketplaceDetail,
    MarketplaceCheckout,
    UserProfile,
    Careers,
    Login
}

class RepairViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = AppRepository(database.appDao())

    // Supabase Dynamic Catalog State Flow
    val brandsList = MutableStateFlow<List<SupabaseBrand>>(emptyList())
    val devicesList = MutableStateFlow<List<SupabaseDevice>>(emptyList())
    val repairTypesList = MutableStateFlow<List<SupabaseRepairType>>(emptyList())
    val partsList = MutableStateFlow<List<SupabasePart>>(emptyList())
    val isCatalogLoading = MutableStateFlow(false)

    init {
        loadSupabaseCatalog()
    }

    fun loadSupabaseCatalog() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            isCatalogLoading.value = true
            try {
                val b = SupabaseClient.fetchBrands()
                val d = SupabaseClient.fetchDevices()
                val rt = SupabaseClient.fetchRepairTypes()
                val p = SupabaseClient.fetchParts()
                
                brandsList.value = b
                devicesList.value = d
                repairTypesList.value = rt
                partsList.value = p
                Log.d("RepairViewModel", "Successfully fetched all Supabase tables in app: ${b.size} brands, ${d.size} devices, ${p.size} parts.")
            } catch (e: Exception) {
                Log.e("RepairViewModel", "Error fetching dynamic catalog tables from Supabase in app", e)
            } finally {
                isCatalogLoading.value = false
            }
        }
    }

    // UI state streams
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val allRepairRequests: StateFlow<List<RepairRequest>> = repository.allRepairRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allParts: StateFlow<List<Part>> = repository.allParts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allListings: StateFlow<List<Listing>> = repository.allListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobPostings: StateFlow<List<JobPosting>> = repository.allJobPostings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<Review>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val promotions: StateFlow<List<Promotion>> = repository.promotions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertPromotion(title: String, description: String, offerPrice: Double, drawableResName: String = "img_dead_phone_offer_1782763644093") {
        viewModelScope.launch {
            repository.insertPromotion(
                Promotion(
                    title = title,
                    description = description,
                    offerPrice = offerPrice,
                    drawableResName = drawableResName
                )
            )
        }
    }

    fun deletePromotion(id: Long) {
        viewModelScope.launch {
            repository.deletePromotion(id)
        }
    }

    // Navigation and Selection States
    var isLoggedIn = mutableStateOf(false)
    var currentScreen = mutableStateOf(Screen.Landing)
    var selectedRequestId = mutableStateOf<Long?>(null)
    var selectedListingId = mutableStateOf<Long?>(null)

    val allUserProfiles: StateFlow<List<UserProfile>> = repository.allUserProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Standard Sign Up
    fun registerUser(name: String, phone: String, city: String, role: String = "CUSTOMER", password: String, confirmPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (name.isBlank() || phone.isBlank() || city.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                onError("Please fill in all required fields!")
                return@launch
            }
            if (password != confirmPassword) {
                onError("Passwords do not match!")
                return@launch
            }
            val existing = repository.getUserProfileByPhone(phone)
            if (existing != null) {
                onError("This phone number is already registered. Please Sign In!")
                return@launch
            }
            // Create profile
            val newProfile = UserProfile(
                id = phone, // unique key for this account
                name = name,
                phone = phone,
                city = city,
                role = role,
                password = password,
                email = "${name.lowercase().replace(" ", "")}@gmail.com"
            )
            repository.updateUserProfile(newProfile)
            
            // Also set as current user
            val currentUser = newProfile.copy(id = "current_user")
            repository.updateUserProfile(currentUser)
            
            isLoggedIn.value = true
            currentScreen.value = when (role) {
                "CUSTOMER" -> Screen.CustomerDashboard
                "MARKETPLACE_BUYER" -> Screen.Marketplace
                "TECHNICIAN" -> Screen.TechnicianDashboard
                "REPAIRMASTER" -> Screen.RepairMasterDashboard
                "COORDINATOR" -> Screen.CoordinatorDashboard
                "ADMIN" -> Screen.AdminDashboard
                else -> Screen.CustomerDashboard
            }
            onSuccess()
        }
    }

    // Standard Sign In
    fun signInUser(phone: String, passwordEntered: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (phone.isBlank()) {
                onError("Please enter your Nagpur Mobile Number!")
                return@launch
            }
            if (passwordEntered.isBlank()) {
                onError("Please enter your Password!")
                return@launch
            }

            val normalizedPhone = phone.filter { it.isDigit() }
            var existing = repository.getUserProfileByPhone(phone)

            // Auto-repair/ensure preseeded demo accounts on login to prevent locking out
            if (normalizedPhone.endsWith("9522502707")) {
                val adminPhone = "+91 95225 02707"
                if (existing == null) {
                    val newAdmin = UserProfile(
                        id = adminPhone,
                        role = "ADMIN",
                        name = "Admin Owner",
                        phone = adminPhone,
                        email = "admin.owner@gmail.com",
                        city = "Nagpur",
                        password = "admin@rm2024"
                    )
                    repository.updateUserProfile(newAdmin)
                    existing = newAdmin
                } else if (existing.password != "admin@rm2024" || existing.role != "ADMIN") {
                    val updatedAdmin = existing.copy(
                        role = "ADMIN",
                        password = "admin@rm2024"
                    )
                    repository.updateUserProfile(updatedAdmin)
                    existing = updatedAdmin
                }
            } else if (normalizedPhone.endsWith("9823055555")) {
                val techPhone = "+91 98230 55555"
                if (existing == null) {
                    val newTech = UserProfile(
                        id = techPhone,
                        role = "TECHNICIAN",
                        name = "Amit Sharma",
                        phone = techPhone,
                        email = "amit.sharma@gmail.com",
                        city = "Nagpur",
                        password = "tech@rm2024",
                        technicianStatus = "Approved"
                    )
                    repository.updateUserProfile(newTech)
                    existing = newTech
                } else if (existing.password != "tech@rm2024" || existing.role != "TECHNICIAN" || existing.technicianStatus != "Approved") {
                    val updatedTech = existing.copy(
                        role = "TECHNICIAN",
                        password = "tech@rm2024",
                        technicianStatus = "Approved"
                    )
                    repository.updateUserProfile(updatedTech)
                    existing = updatedTech
                }
            } else if (normalizedPhone.endsWith("9823077777")) {
                val masterPhone = "+91 98230 77777"
                if (existing == null) {
                    val newMaster = UserProfile(
                        id = masterPhone,
                        role = "REPAIRMASTER",
                        name = "Vinay Patel",
                        phone = masterPhone,
                        email = "vinay.patel@gmail.com",
                        city = "Nagpur",
                        password = "master@rm2024",
                        repairMasterStatus = "Approved"
                    )
                    repository.updateUserProfile(newMaster)
                    existing = newMaster
                } else if (existing.password != "master@rm2024" || existing.role != "REPAIRMASTER" || existing.repairMasterStatus != "Approved") {
                    val updatedMaster = existing.copy(
                        role = "REPAIRMASTER",
                        password = "master@rm2024",
                        repairMasterStatus = "Approved"
                    )
                    repository.updateUserProfile(updatedMaster)
                    existing = updatedMaster
                }
            } else if (normalizedPhone.endsWith("9823012345")) {
                val custPhone = "+91 98230 12345"
                if (existing == null) {
                    val newCust = UserProfile(
                        id = custPhone,
                        role = "CUSTOMER",
                        name = "Rajesh Deshmukh",
                        phone = custPhone,
                        email = "rajesh.deshmukh@gmail.com",
                        city = "Nagpur",
                        password = "12345"
                    )
                    repository.updateUserProfile(newCust)
                    existing = newCust
                } else if (existing.password != "12345" || existing.role != "CUSTOMER") {
                    val updatedCust = existing.copy(
                        role = "CUSTOMER",
                        password = "12345"
                    )
                    repository.updateUserProfile(updatedCust)
                    existing = updatedCust
                }
            }

            if (existing == null) {
                onError("Nagpur mobile number not registered. Please Sign Up!")
                return@launch
            }

            if (existing.password != passwordEntered) {
                onError("Incorrect password. Please try again!")
                return@launch
            }

            // Employee authorization checks
            if (existing.role == "TECHNICIAN" && existing.technicianStatus != "Approved") {
                when (existing.technicianStatus) {
                    "Pending" -> onError("Your application is currently pending admin review. Please wait for authorization!")
                    "Rejected" -> onError("Your application has been rejected by the administrator.")
                    else -> onError("You must apply for a Technician position first and be approved!")
                }
                return@launch
            }

            if (existing.role == "REPAIRMASTER" && existing.repairMasterStatus != "Approved") {
                when (existing.repairMasterStatus) {
                    "Pending" -> onError("Your store application is currently pending admin review. Please wait for authorization!")
                    "Rejected" -> onError("Your application has been rejected by the administrator.")
                    else -> onError("You must apply for a Repair Master position first and be approved!")
                }
                return@launch
            }

            // Copy to active current user
            val currentUser = existing.copy(id = "current_user")
            repository.updateUserProfile(currentUser)
            
            isLoggedIn.value = true
            currentScreen.value = when (existing.role) {
                "CUSTOMER" -> Screen.CustomerDashboard
                "MARKETPLACE_BUYER" -> Screen.Marketplace
                "TECHNICIAN" -> Screen.TechnicianDashboard
                "REPAIRMASTER" -> Screen.RepairMasterDashboard
                "COORDINATOR" -> Screen.CoordinatorDashboard
                "ADMIN" -> Screen.AdminDashboard
                else -> Screen.CustomerDashboard
            }
            onSuccess()
        }
    }

    // Google Sign-In or Sign-Up Flow
    fun handleGoogleAuth(email: String, name: String, phoneIfSignUp: String = "", cityIfSignUp: String = "", onSuccess: (needMoreInfo: Boolean) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val existing = repository.getUserProfileById(email)
            if (existing != null) {
                // Already registered! Just log them in.
                val currentUser = existing.copy(id = "current_user")
                repository.updateUserProfile(currentUser)
                isLoggedIn.value = true
                currentScreen.value = when (existing.role) {
                    "CUSTOMER" -> Screen.CustomerDashboard
                    "MARKETPLACE_BUYER" -> Screen.Marketplace
                    "TECHNICIAN" -> Screen.TechnicianDashboard
                    "REPAIRMASTER" -> Screen.RepairMasterDashboard
                    "COORDINATOR" -> Screen.CoordinatorDashboard
                    "ADMIN" -> Screen.AdminDashboard
                    else -> Screen.CustomerDashboard
                }
                onSuccess(false)
            } else {
                // Not registered. Do we have phone and city to complete registration?
                if (phoneIfSignUp.isBlank() || cityIfSignUp.isBlank()) {
                    // Need more info to complete sign-up
                    onSuccess(true)
                } else {
                    // Have info, create the account!
                    val newProfile = UserProfile(
                        id = email,
                        name = name,
                        phone = phoneIfSignUp,
                        city = cityIfSignUp,
                        email = email,
                        role = "CUSTOMER"
                    )
                    repository.updateUserProfile(newProfile)

                    // Set as active
                    val currentUser = newProfile.copy(id = "current_user")
                    repository.updateUserProfile(currentUser)

                    isLoggedIn.value = true
                    currentScreen.value = Screen.CustomerDashboard
                    onSuccess(false)
                }
            }
        }
    }

    // Logout
    fun logout() {
        viewModelScope.launch {
            isLoggedIn.value = false
            currentScreen.value = Screen.Landing
        }
    }

    // Filter states
    var marketplaceSearch = mutableStateOf("")
    var marketplaceGradeFilter = mutableStateOf("All")
    var coordinatorSearch = mutableStateOf("")
    var coordinatorFilter = mutableStateOf("All") // All, Unassigned, Active, Completed

    // Tab positions for dashboards
    var adminSelectedTab = mutableStateOf(0)
    var repairMasterSelectedTab = mutableStateOf(0)

    init {
        // Automatically populate initial data if database is brand new
        repository.prepopulateIfEmpty()
    }

    // Navigation triggers
    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun selectRequest(id: Long) {
        selectedRequestId.value = id
        navigateTo(Screen.RepairRequestDetail)
    }

    fun selectListing(id: Long) {
        selectedListingId.value = id
        navigateTo(Screen.MarketplaceDetail)
    }

    fun selectListingCheckout(id: Long) {
        selectedListingId.value = id
        navigateTo(Screen.MarketplaceCheckout)
    }

    // Core Business Logic

    // Update Profile Information
    fun updateProfile(name: String, phone: String) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.updateUserProfile(current.copy(name = name, phone = phone))
        }
    }

    // Role switcher with passcode locks
    fun switchRole(newRole: String, passcode: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val dbProfile = repository.getUserProfileByPhone(userProfile.value.phone)
            val assignedRoles = dbProfile?.role?.split(",")?.map { it.trim().uppercase() } ?: emptyList()
            val isAlreadyAuthorized = assignedRoles.contains(newRole.uppercase())

            // Strict Admin Security Check: Nobody can switch to ADMIN unless they are already officially ADMIN in the database.
            if (newRole == "ADMIN") {
                if (dbProfile == null || !assignedRoles.contains("ADMIN")) {
                    onError("Access Denied! You must be registered as an ADMIN in the database to use this role.")
                    return@launch
                }
            }

            val isPasscodeValid = isAlreadyAuthorized || when (newRole) {
                "CUSTOMER", "MARKETPLACE_BUYER" -> true
                "TECHNICIAN" -> passcode == "tech@rm2024"
                "REPAIRMASTER" -> passcode == "master@rm2024"
                "COORDINATOR" -> passcode == "coord@rm2024"
                "ADMIN" -> passcode == "admin@rm2024"
                else -> false
            }

            if (isPasscodeValid) {
                val current = userProfile.value
                val updatedCurrent = current.copy(role = newRole)
                repository.updateUserProfile(updatedCurrent)

                // Add to persistent DB profile list if they used a valid passcode
                if (dbProfile != null && !isAlreadyAuthorized) {
                    val currentRoles = dbProfile.role.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                    if (!currentRoles.contains(newRole)) {
                        currentRoles.add(newRole)
                    }
                    repository.updateUserProfile(dbProfile.copy(role = currentRoles.joinToString(",")))
                }

                currentScreen.value = when (newRole) {
                    "CUSTOMER" -> Screen.CustomerDashboard
                    "TECHNICIAN" -> Screen.TechnicianDashboard
                    "REPAIRMASTER" -> Screen.RepairMasterDashboard
                    "COORDINATOR" -> Screen.CoordinatorDashboard
                    "ADMIN" -> Screen.AdminDashboard
                    "MARKETPLACE_BUYER" -> Screen.Marketplace
                    else -> Screen.CustomerDashboard
                }
                onSuccess()
            } else {
                onError("Access Denied! Incorrect passcode for $newRole.")
            }
        }
    }

    // Customer: Book new repair
    fun createRepairRequest(brand: String, model: String, issue: String, city: String, partsTotal: Double = 0.0, serviceCharge: Double = 0.0, total: Double = 0.0) {
        viewModelScope.launch {
            val codeNum = (1000..9999).random()
            val request = RepairRequest(
                code = "RM-$codeNum",
                deviceBrand = brand,
                deviceModel = model,
                issueDescription = issue,
                city = city,
                customerName = userProfile.value.name,
                customerPhone = userProfile.value.phone,
                statusStep = 1, // Placed
                totalAmount = total,
                quoteServiceCharge = serviceCharge,
                quotePartsString = if (partsTotal > 0.0) "Parts Estimate,$partsTotal,1" else ""
            )
            repository.insertRepairRequest(request)
            navigateTo(Screen.CustomerDashboard)
        }
    }

    // Customer: Cancel repair
    fun deleteRequest(requestId: Long) {
        viewModelScope.launch {
            repository.deleteRepairRequest(requestId)
            navigateTo(Screen.CustomerDashboard)
        }
    }

    // Customer: Approve quote
    fun approveQuotation(requestId: Long) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                repository.updateRepairRequest(
                    current.copy(
                        isApprovedByCustomer = true,
                        statusStep = 7 // Quotation Approved - Repairing
                    )
                )
            }
        }
    }

    // Customer: Reject quote
    fun rejectQuotation(requestId: Long) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                repository.updateRepairRequest(
                    current.copy(
                        isApprovedByCustomer = false,
                        statusStep = 11 // Completed / Terminated
                    )
                )
            }
        }
    }

    // Technician/Coordinator: Advance repair status
    fun advanceRepairStatus(requestId: Long, nextStep: Int) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                repository.updateRepairRequest(current.copy(statusStep = nextStep))
            }
        }
    }

    fun setMaintenanceMode(requestId: Long, enabled: Boolean) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                repository.updateRepairRequest(current.copy(isMaintenanceModeEnabled = enabled))
            }
        }
    }

    fun setDeviceDeadOrBlank(requestId: Long, isDead: Boolean) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                repository.updateRepairRequest(current.copy(isDeviceDeadOrBlank = isDead))
            }
        }
    }

    // OTP: Verify pickup OTP (advances from 3 to 4)
    fun verifyPickupOtp(requestId: Long, enteredOtp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                if (current.pickupOtpCode == enteredOtp) {
                    repository.updateRepairRequest(
                        current.copy(
                            isPickupOtpVerified = true,
                            statusStep = 4 // Device Received at Workshop
                        )
                    )
                    onSuccess()
                } else {
                    onError("Invalid pickup verification code!")
                }
            }
        }
    }

    // OTP: Verify delivery OTP (advances from 10 to 11)
    fun verifyDeliveryOtp(requestId: Long, enteredOtp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                if (current.deliveryOtpCode == enteredOtp) {
                    repository.updateRepairRequest(
                        current.copy(
                            isDeliveryOtpVerified = true,
                            statusStep = 11 // Handed Over
                        )
                    )
                    onSuccess()
                } else {
                    onError("Invalid delivery verification code!")
                }
            }
        }
    }

    // Coordinator: Prepare Pre-Quote and Route to Repair Master
    fun routePreQuote(requestId: Long, partsString: String, serviceCharge: Double, repairMasterId: Long) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                // Calculate total based on partsString and serviceCharge
                val parts = partsString.split(";").filter { it.isNotBlank() }.map {
                    val split = it.split(",")
                    val price = split.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                    val qty = split.getOrNull(2)?.toIntOrNull() ?: 1
                    price * qty
                }
                val subtotal = parts.sum()
                val taxFee = (subtotal + serviceCharge) * (current.serviceChargePercent / 100.0)
                val total = subtotal + serviceCharge + taxFee

                repository.updateRepairRequest(
                    current.copy(
                        quotePartsString = partsString,
                        quoteServiceCharge = serviceCharge,
                        totalAmount = total,
                        repairMasterId = repairMasterId
                    )
                )
            }
        }
    }

    // Coordinator: Assign technician to request (advances from 1 to 2)
    fun assignTechnician(requestId: Long, techId: Long, techName: String) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                repository.updateRepairRequest(
                    current.copy(
                        technicianId = techId,
                        technicianName = techName,
                        statusStep = 2 // Technician Assigned
                    )
                )
            }
        }
    }

    // Coordinator: Self-assign both as Technician and Repair Master simultaneously (advances to 2)
    fun selfAssignBoth(requestId: Long, coordinatorName: String) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                repository.updateRepairRequest(
                    current.copy(
                        technicianId = 999L,
                        technicianName = coordinatorName,
                        repairMasterId = 999L,
                        statusStep = 2 // Technician Assigned
                    )
                )
            }
        }
    }

    // Technician: Build and Submit Quotation (advances from 5 to 6)
    fun submitQuotation(requestId: Long, selectedParts: List<Pair<Part, Int>>, serviceCharge: Double) {
        viewModelScope.launch {
            val requests = allRepairRequests.value
            val current = requests.find { it.id == requestId }
            if (current != null) {
                // Construct format: "Name,Price,Qty;Name,Price,Qty"
                val partsString = selectedParts.joinToString(";") { "${it.first.name},${it.first.price},${it.second}" }
                val subtotal = selectedParts.sumOf { it.first.price * it.second }
                val taxFee = (subtotal + serviceCharge) * (current.serviceChargePercent / 100.0)
                val total = subtotal + serviceCharge + taxFee

                repository.updateRepairRequest(
                    current.copy(
                        quotePartsString = partsString,
                        quoteServiceCharge = serviceCharge,
                        totalAmount = total,
                        statusStep = 6 // Quotation Awaiting Customer Approval
                    )
                )
            }
        }
    }

    // RepairMaster: Create Marketplace Listing
    fun createListing(title: String, grade: String, storage: String, price: Double, city: String, description: String, imageUrl: String) {
        viewModelScope.launch {
            val listing = Listing(
                title = title,
                grade = grade,
                storage = storage,
                price = price,
                city = city,
                description = description,
                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500" }
            )
            repository.insertListing(listing)
        }
    }

    // RepairMaster: Delete Marketplace Listing
    fun deleteListing(listingId: Long) {
        viewModelScope.launch {
            repository.deleteListing(listingId)
        }
    }

    // Buyer: Purchase a listing
    fun purchaseListing(listingId: Long, buyerName: String, buyerPhone: String, buyerAddress: String, price: Double, onCompleted: () -> Unit) {
        viewModelScope.launch {
            val listingFlow = repository.getListingById(listingId)
            val listing = listingFlow.firstOrNull()
            if (listing != null) {
                // 1. Mark listing as sold
                repository.updateListing(listing.copy(isSold = true))
                // 2. Insert Order record
                val order = Order(
                    listingId = listingId,
                    listingTitle = listing.title,
                    buyerName = buyerName,
                    buyerPhone = buyerPhone,
                    buyerAddress = buyerAddress,
                    amountPaid = price
                )
                repository.insertOrder(order)
                onCompleted()
            }
        }
    }

    // Admin: Toggle Application Approvals by phone
    fun approveApplication(phone: String, type: String) {
        viewModelScope.launch {
            val profile = repository.getUserProfileByPhone(phone)
            if (profile != null) {
                if (type.contains("Technician", ignoreCase = true)) {
                    repository.updateUserProfile(
                        profile.copy(
                            isTechnicianApplied = true,
                            technicianStatus = "Approved",
                            role = "TECHNICIAN"
                        )
                    )
                } else if (type.contains("Repair Master", ignoreCase = true)) {
                    repository.updateUserProfile(
                        profile.copy(
                            isRepairMasterApplied = true,
                            repairMasterStatus = "Approved",
                            role = "REPAIRMASTER"
                        )
                    )
                }
            }
        }
    }

    fun rejectApplication(phone: String, type: String) {
        viewModelScope.launch {
            val profile = repository.getUserProfileByPhone(phone)
            if (profile != null) {
                if (type.contains("Technician", ignoreCase = true)) {
                    repository.updateUserProfile(profile.copy(technicianStatus = "Rejected"))
                } else if (type.contains("Repair Master", ignoreCase = true)) {
                    repository.updateUserProfile(profile.copy(repairMasterStatus = "Rejected"))
                }
            }
        }
    }

    fun updateUserRole(phone: String, newRole: String) {
        viewModelScope.launch {
            val profile = repository.getUserProfileByPhone(phone)
            if (profile != null) {
                // Safeguard: Do not allow altering own admin role if you are the master owner
                if (profile.phone == "+91 95225 02707" && newRole != "ADMIN") {
                    return@launch
                }
                repository.updateUserProfile(profile.copy(role = newRole))
            }
        }
    }

    fun updateUserRolesList(phone: String, roles: List<String>) {
        viewModelScope.launch {
            val profile = repository.getUserProfileByPhone(phone)
            if (profile != null) {
                // Safeguard: Do not allow altering own admin role if you are the master owner
                if (profile.phone == "+91 95225 02707" && !roles.contains("ADMIN")) {
                    return@launch
                }
                val rolesString = roles.joinToString(",")
                repository.updateUserProfile(profile.copy(role = rolesString))
            }
        }
    }

    // Admin: Add custom spare part
    fun addPart(name: String, price: Double, category: String) {
        viewModelScope.launch {
            repository.insertPart(Part(name = name, price = price, category = category))
        }
    }

    // Admin/Coordinator: Update existing spare part
    fun updatePart(partId: Long, name: String, price: Double, category: String) {
        viewModelScope.launch {
            repository.insertPart(Part(id = partId, name = name, price = price, category = category))
        }
    }

    // Admin: Delete custom spare part
    fun deletePart(partId: Long) {
        viewModelScope.launch {
            repository.deletePart(partId)
        }
    }

    // Admin: Create job postings
    fun createJobPosting(title: String, roleType: String, location: String, description: String) {
        viewModelScope.launch {
            repository.insertJobPosting(
                JobPosting(
                    title = title,
                    roleType = roleType,
                    location = location,
                    description = description
                )
            )
        }
    }

    fun closeJobPosting(postingId: Long) {
        viewModelScope.launch {
            val postings = allJobPostings.value
            val current = postings.find { it.id == postingId }
            if (current != null) {
                repository.updateJobPosting(current.copy(isClosed = true))
            }
        }
    }

    fun deleteJobPosting(postingId: Long) {
        viewModelScope.launch {
            repository.deleteJobPosting(postingId)
        }
    }

    // Careers: Apply for role with password and city
    fun applyForRole(role: String, applicantName: String, applicantPhone: String, city: String, password: String) {
        viewModelScope.launch {
            val newProfile = UserProfile(
                id = applicantPhone,
                phone = applicantPhone,
                name = applicantName,
                city = city,
                password = password,
                role = if (role.contains("Technician", ignoreCase = true)) "TECHNICIAN" else "REPAIRMASTER",
                technicianStatus = if (role.contains("Technician", ignoreCase = true)) "Pending" else "None",
                repairMasterStatus = if (role.contains("Repair Master", ignoreCase = true)) "Pending" else "None",
                isTechnicianApplied = if (role.contains("Technician", ignoreCase = true)) true else false,
                isRepairMasterApplied = if (role.contains("Repair Master", ignoreCase = true)) true else false
            )
            repository.updateUserProfile(newProfile)
        }
    }

    fun submitReview(
        repairRequestId: Long,
        customerName: String,
        technicianId: Long?,
        technicianName: String?,
        techRating: Int,
        masterRating: Int,
        comment: String
    ) {
        viewModelScope.launch {
            repository.insertReview(
                Review(
                    repairRequestId = repairRequestId,
                    customerName = customerName,
                    technicianId = technicianId,
                    technicianName = technicianName,
                    techRating = techRating,
                    masterRating = masterRating,
                    comment = comment
                )
            )
        }
    }

    fun clearAllDataAndPrepareLaunch(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllTablesAndPrepareLaunch()
            onComplete()
        }
    }
}
