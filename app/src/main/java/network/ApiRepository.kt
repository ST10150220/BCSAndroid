package network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository {
    private val api = ApiClient.instance.create(ApiService::class.java)

    fun fetchInvoices(onResult: (List<Invoice>?) -> Unit) {
        api.getAllInvoices().enqueue(object : Callback<List<Invoice>> {
            override fun onResponse(call: Call<List<Invoice>>, response: Response<List<Invoice>>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<List<Invoice>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun sendMessage(message: Message, onResult: (Message?) -> Unit) {
        api.sendMessage(message).enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                onResult(null)
            }
        })
    }
}
