package network

import network.ApiService.Message

data class MessagesResponse(
    val success: Boolean,
    val data: List<Message>
)
