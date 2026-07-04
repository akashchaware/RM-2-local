package com.example.data

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

data class SupabaseBrand(val id: String, val name: String)
data class SupabaseDevice(val id: String, val brand_id: String, val name: String)
data class SupabaseRepairType(val id: String, val name: String, val label: String?)
data class SupabasePart(val device_id: String, val repair_type_id: String, val name: String, val price: Double)

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    private const val SUPABASE_URL = "https://mpcnfrshpgcpmrgledwy.supabase.co"
    private const val ANON_KEY = "sb_publishable_IlSzuHbWowZ84IdxRwBCxg_DDT9P_Vz"

    private val client = OkHttpClient.Builder().build()
    
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // Robust Fallbacks in case of network issues
    val fallbackBrands = listOf(
        SupabaseBrand("b1", "Apple"), SupabaseBrand("b2", "Samsung"), SupabaseBrand("b3", "Vivo"),
        SupabaseBrand("b4", "OnePlus"), SupabaseBrand("b5", "Xiaomi (MI)"), SupabaseBrand("b6", "Oppo"),
        SupabaseBrand("b7", "Realme"), SupabaseBrand("b8", "Google"), SupabaseBrand("b9", "Nothing"),
        SupabaseBrand("b10", "Motorola"), SupabaseBrand("b11", "iQOO"), SupabaseBrand("b12", "Lava")
    )

    val fallbackDevices = listOf(
        SupabaseDevice("d1", "b1", "iPhone 15 Pro Max"),
        SupabaseDevice("d2", "b1", "iPhone 15"),
        SupabaseDevice("d3", "b3", "Vivo V30 Pro"),
        SupabaseDevice("d4", "b3", "Vivo V29 Pro"),
        SupabaseDevice("d5", "b2", "Galaxy S24 Ultra"),
        SupabaseDevice("d6", "b2", "Galaxy A55"),
        SupabaseDevice("d7", "b4", "OnePlus 12"),
        SupabaseDevice("d8", "b5", "Redmi Note 13 Pro"),
        SupabaseDevice("d9", "b6", "Reno 11 Pro"),
        SupabaseDevice("d10", "b7", "GT 6 Pro"),
        SupabaseDevice("d11", "b8", "Pixel 8 Pro"),
        SupabaseDevice("d12", "b9", "Phone 2"),
        SupabaseDevice("d13", "b10", "Edge 50 Pro"),
        SupabaseDevice("d14", "b11", "iQOO 12"),
        SupabaseDevice("d15", "b12", "Agni 2")
    )

    val fallbackRepairTypes = listOf(
        SupabaseRepairType("rt1", "screen", "📱 Screen Replacement"),
        SupabaseRepairType("rt2", "battery", "🔋 Battery Replacement"),
        SupabaseRepairType("rt3", "chargingport", "🔌 Charging Port Repair"),
        SupabaseRepairType("rt4", "camera", "📷 Camera Repair"),
        SupabaseRepairType("rt5", "speaker", "🔊 Speaker / Mic Repair"),
        SupabaseRepairType("rt6", "button", "🔘 Button Repair"),
        SupabaseRepairType("rt7", "motherboard", "💻 Motherboard Repair"),
        SupabaseRepairType("rt8", "waterdamage", "💧 Water Damage Repair"),
        SupabaseRepairType("rt9", "software", "📀 Software / OS Repair"),
        SupabaseRepairType("rt10", "network", "📶 Network / Antenna Repair"),
        SupabaseRepairType("rt11", "completeoverhaul", "⚙️ Complete Overhaul")
    )

    val fallbackParts = mutableListOf(
        SupabasePart("d3", "rt1", "AMOLED Screen Panel Assembly", 6300.0),
        SupabasePart("d3", "rt1", "Digitizer & Display Flex", 504.0),
        SupabasePart("d3", "rt1", "Water-Resistant Frame Adhesive Seal", 126.0),
        SupabasePart("d3", "rt2", "Certified Li-Po 5000mAh Battery Cell", 1500.0),
        SupabasePart("d3", "rt2", "Thermal Dissipation Pad", 150.0),
        
        SupabasePart("d1", "rt1", "Super Retina XDR OLED Display", 25200.0),
        SupabasePart("d1", "rt1", "Force Touch Digitizer Sensor", 2016.0),
        SupabasePart("d1", "rt1", "IP68 Watertight Perimeter Seal Adhesive", 504.0),
        SupabasePart("d1", "rt2", "OEM Battery Cell Replacement", 4500.0),
        
        SupabasePart("d5", "rt1", "Dynamic AMOLED 2X Display Module", 21500.0),
        SupabasePart("d5", "rt1", "Corning Gorilla Armor Glass Layer", 3200.0),
        SupabasePart("d5", "rt2", "Official Li-Ion 5000mAh Battery Pack", 2800.0),
        SupabasePart("d5", "rt3", "USB-C SuperFast Charging Port PCB", 1400.0),
        SupabasePart("d5", "rt7", "Logic Board IC Power Management Chip", 5500.0)
    )

    init {
        // Auto-populate fallbacks for nice coverage
        fallbackDevices.forEach { device ->
            fallbackRepairTypes.forEach { rt ->
                val exists = fallbackParts.any { it.device_id == device.id && it.repair_type_id == rt.id }
                if (!exists) {
                    var baseP = 1500.0
                    when (rt.name) {
                        "screen" -> baseP = 4200.0
                        "battery" -> baseP = 1600.0
                        "motherboard" -> baseP = 5000.0
                        "chargingport" -> baseP = 1000.0
                    }
                    if (device.brand_id == "b1") baseP *= 1.5
                    else if (device.brand_id == "b2") baseP *= 1.2

                    fallbackParts.add(SupabasePart(
                        device_id = device.id,
                        repair_type_id = rt.id,
                        name = "Premium ${rt.name.uppercase()} Replacement Kit",
                        price = Math.round(baseP).toDouble()
                    ))
                }
            }
        }
    }

    private fun <T> fetchTable(tableName: String, elementClass: Class<T>): List<T> {
        val url = "$SUPABASE_URL/rest/v1/$tableName?select=*"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", ANON_KEY)
            .addHeader("Authorization", "Bearer $ANON_KEY")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "Unsuccessful response for $tableName: ${response.code}")
                    return emptyList()
                }
                val bodyString = response.body?.string() ?: return emptyList()
                val listType = Types.newParameterizedType(List::class.java, elementClass)
                val adapter = moshi.adapter<List<T>>(listType)
                adapter.fromJson(bodyString) ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching $tableName from Supabase", e)
            emptyList()
        }
    }

    fun fetchBrands(): List<SupabaseBrand> {
        val remote = fetchTable("brands", SupabaseBrand::class.java)
        return if (remote.isNotEmpty()) remote else fallbackBrands
    }

    fun fetchDevices(): List<SupabaseDevice> {
        val remote = fetchTable("devices", SupabaseDevice::class.java)
        return if (remote.isNotEmpty()) remote else fallbackDevices
    }

    fun fetchRepairTypes(): List<SupabaseRepairType> {
        val remote = fetchTable("repair_types", SupabaseRepairType::class.java)
        return if (remote.isNotEmpty()) remote else fallbackRepairTypes
    }

    fun fetchParts(): List<SupabasePart> {
        val remote = fetchTable("parts", SupabasePart::class.java)
        return if (remote.isNotEmpty()) remote else fallbackParts
    }
}
