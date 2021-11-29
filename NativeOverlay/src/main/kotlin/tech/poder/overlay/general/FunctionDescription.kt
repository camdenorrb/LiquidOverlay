package tech.poder.overlay.general

data class FunctionDescription(
    val name: String,
    val returnType: Class<*>? = null,
    val params: List<Class<*>> = emptyList()
)