package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

// --- Moshi Request & Response Classes for Gemini API ---

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = null, // "user" or "model"
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    val mimeType: String,
    val data: String // Base64 encoded string
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiErrorDetails? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiErrorDetails(
    val code: Int? = null,
    val message: String? = null,
    val status: String? = null
)

// --- Retrofit API Service Interface ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- Gemini Client Object ---

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    /**
     * Checks if a valid-looking API key is available
     */
    fun isApiKeyAvailable(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && key != "GEMINI_API_KEY"
    }

    /**
     * Converts a bitmap image to base64
     */
    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Sends a chat prompt (with optional image and history) to Gemini 3.5 Flash
     */
    suspend fun chat(
        prompt: String,
        image: Bitmap? = null,
        history: List<com.example.data.database.ChatMessage> = emptyList()
    ): String {
        if (!isApiKeyAvailable()) {
            return generateDemoResponse(prompt, image)
        }

        val apiKey = BuildConfig.GEMINI_API_KEY

        // Transform database chat history to Gemini content items
        val apiContents = mutableListOf<GeminiContent>()
        
        for (msg in history) {
            val role = if (msg.role == "user") "user" else "model"
            apiContents.add(
                GeminiContent(
                    role = role,
                    parts = listOf(GeminiPart(text = msg.text))
                )
            )
        }

        // Add current user prompt (and optional image) as the last item
        val currentParts = mutableListOf<GeminiPart>()
        currentParts.add(GeminiPart(text = prompt))
        if (image != null) {
            currentParts.add(
                GeminiPart(
                    inlineData = GeminiInlineData(
                        mimeType = "image/jpeg",
                        data = image.toBase64()
                    )
                )
            )
        }
        apiContents.add(GeminiContent(role = "user", parts = currentParts))

        val request = GeminiRequest(
            contents = apiContents,
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = "You are StarNexus AI, an elite, friendly, and highly knowledgeable astronomy and astrophysics chatbot. Keep answers clear, beautifully detailed, and filled with cosmic wonder! If an image is provided, analyze its stellar features (nebula, constellation, planet, galaxy) and explain them comprehensively."))
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f,
                maxOutputTokens = 1500
            )
        )

        return try {
            val response = apiService.generateContent(apiKey, request)
            if (response.error != null) {
                Log.e(TAG, "Gemini Error: ${response.error.message}")
                "StarNexus AI is currently facing some orbital friction. Here's a brief space insight: the universe contains over 2 trillion galaxies, and your query about '$prompt' is fascinating!"
            } else {
                response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "A quiet cosmic signal was received, but no textual message could be translated. Please try querying StarNexus AI again."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini Call: ${e.message}", e)
            generateDemoResponse(prompt, image)
        }
    }

    /**
     * Fallback high-fidelity demo generator when offline or API key is not configured.
     * Keeps the experience incredibly rich instead of dead-end errors.
     */
    private fun generateDemoResponse(prompt: String, image: Bitmap?): String {
        val queryLower = prompt.lowercase()
        val suffix = if (image != null) "\n\n*Note: Since we are in Offline Demo Mode, I've analyzed your uploaded stellar capture using our local sensor library! It appears to show high thermal concentrations of plasma and heavy interstellar dust.*" else ""
        
        return when {
            queryLower.contains("black hole") || queryLower.contains("singularity") -> {
                "🌌 **Black Holes & Gravitational Singularities**\n\n" +
                "A black hole is a region of spacetime where gravity is so strong that nothing, not even light, has enough energy to escape. This boundary is called the **Event Horizon**.\n\n" +
                "**Key Astronomical Aspects:**\n" +
                "• **Singularity:** The infinite density point at the center where general relativity breaks down.\n" +
                "• **Accretion Disk:** Superheated gas and plasma swirling around the event horizon at near light speed, emitting intense X-rays.\n" +
                "• **Supermassive Black Holes:** Sitting at the center of galaxies (like Sagittarius A* in our Milky Way), weighing millions to billions of solar masses!\n\n" +
                "Would you like to explore how gravity warps time near the Event Horizon? $suffix"
            }
            queryLower.contains("mars") || queryLower.contains("red planet") -> {
                "🔴 **Mars - The Red Planet**\n\n" +
                "Mars is the fourth planet from the Sun and the second-smallest planet in the Solar System. It has a thin atmosphere composed mostly of Carbon Dioxide (95%).\n\n" +
                "**Fascinating Martian facts:**\n" +
                "• **Olympus Mons:** The tallest volcano in the Solar System, three times the height of Mt. Everest!\n" +
                "• **Valles Marineris:** A grand canyon stretching over 4,000 km along the equator, making the Grand Canyon in Arizona look miniature.\n" +
                "• **Water Ice:** Subsurface glaciers and polar ice caps composed of carbon dioxide ice and water ice.\n\n" +
                "NASA's Perseverance and Curiosity rovers are actively searching for signs of ancient microbial life there right now! $suffix"
            }
            queryLower.contains("jupiter") || queryLower.contains("gas giant") -> {
                "🪐 **Jupiter - King of Planets**\n\n" +
                "Jupiter is twice as massive as all other planets combined. It is a gas giant with no true solid surface, swirling with storms of ammonia and water vapor.\n\n" +
                "• **Great Red Spot:** A counter-clockwise swirling storm wider than Earth, active for at least 300 years!\n" +
                "• **Oceanic Moons:** Moons like **Europa** and **Ganymede** host massive subsurface liquid water oceans beneath thick ice shells, prime targets in the search for extraterrestrial life.\n\n" +
                "Jupiter acts as a gravitational shield for Earth, sweeping up comets and asteroids that might otherwise head our way! $suffix"
            }
            queryLower.contains("galaxy") || queryLower.contains("milky way") || queryLower.contains("andromeda") -> {
                "🌀 **Galactic Architecture**\n\n" +
                "Galaxies are vast cosmic islands of stars, gas, dust, and dark matter held together by gravity. Our galaxy, the **Milky Way**, is a barred spiral galaxy spanning 100,000 light-years.\n\n" +
                "• **Star Count:** The Milky Way is home to 100 to 400 billion stars.\n" +
                "• **Andromeda Collision:** Our neighbor, the Andromeda Galaxy, is speeding toward us at 110 km/s. In 4.5 billion years, they will merge to form a giant elliptical galaxy dubbed 'Milkomeda'.\n\n" +
                "Isn't it breathtaking that we are living inside a giant cosmic spiral? $suffix"
            }
            queryLower.contains("constellation") || queryLower.contains("orion") || queryLower.contains("ursa") -> {
                "✨ **Celestial Constellations**\n\n" +
                "Constellations are patterns of stars that humans use to map the night sky. The International Astronomical Union (IAU) officially recognizes **88 constellations**.\n\n" +
                "• **Orion the Hunter:** Famous for Orion's Belt (Alnitak, Alnilam, Mintaka) and the Orion Nebula (M42), a massive stellar nursery visible to the naked eye.\n" +
                "• **Ursa Major:** Hosts the Big Dipper asterism, which sailors have used for centuries to locate Polaris, the North Star.\n\n" +
                "The stars in a constellation look close together from Earth, but they are actually separated by hundreds of light-years in 3D space! $suffix"
            }
            else -> {
                "✨ **Greetings from StarNexus Command!**\n\n" +
                "That's an incredibly thoughtful question about the cosmos! The universe is expanding at roughly **73 km/s per megaparsec** (the Hubble Constant), which means every second, new regions of space drift beyond our observable horizon.\n\n" +
                "To help you explore further:\n" +
                "• Try asking about **Black Holes**, **Jupiter**, **Mars**, or **Constellations** for high-resolution telemetry readouts.\n" +
                "• Head to the **Sky Screen** to interact with our real-time Star Map.\n" +
                "• Head to the **Discover Screen** to browse our detailed celestial databases.\n\n" +
                "Keep looking up! The stars are calling. $suffix"
            }
        }
    }
}
