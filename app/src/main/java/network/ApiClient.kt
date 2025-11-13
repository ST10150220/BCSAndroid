import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import network.Maintenance
import network.MaintenanceResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    val instance: Retrofit by lazy {
        val gson = GsonBuilder()
            .registerTypeAdapter(MaintenanceResponse::class.java, JsonDeserializer { json, type, context ->
                val jsonObject = json.asJsonObject
                val success = jsonObject["success"].asBoolean
                val dataElement = jsonObject["data"]

                val dataList = if (dataElement.isJsonArray) {
                    context.deserialize<List<Maintenance>>(
                        dataElement,
                        TypeToken.getParameterized(List::class.java, Maintenance::class.java).type
                    )
                } else {
                    listOf(context.deserialize(dataElement, Maintenance::class.java))
                }

                MaintenanceResponse(success, dataList)
            })
            .create()

        Retrofit.Builder()
            .baseUrl("https://brown-construction-services-api.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }
}
