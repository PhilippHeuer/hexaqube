package qube.core.command

data class QubeCommandParameter<T>(
    val name: String,
    val description: String,
    val type: Class<T>,
    val default: T?,
    val options: List<QubeCommandOption>,
    val required: Boolean
)
