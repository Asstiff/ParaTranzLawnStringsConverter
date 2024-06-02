package utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

@JvmOverloads
fun makeHttpRequest(apiKey: String, service: String, serviceId: String = ""): String {
    return try {
        val client = OkHttpClient()
        println("https://paratranz.cn/api/$service" + (if (serviceId.isEmpty()) "" else "/$serviceId"))
        val request = Request.Builder()
            .url("https://paratranz.cn/api/$service" + (if (serviceId.isEmpty()) "" else "/$serviceId"))

            .addHeader("Authorization", apiKey)
            .build()

        val response: Response = client.newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.string() ?: "No response body"
        } else {
            "Request failed with code: ${response.code}"
        }
    } catch (e: Exception) {
        "Request failed: ${e.message}"
    }
}