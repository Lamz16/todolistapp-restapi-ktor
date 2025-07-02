package core

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val status: Int,
    val message: String
)
