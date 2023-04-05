package qube.core.command

interface QubeCommand {

    /**
     * the name of the command
     */
    fun name(): String

    /**
     * a list of aliases for the command
     */
    fun aliases(): List<String>

    /**
     * the description of the command
     */
    fun description(): String

    /**
     * the parameters of the command
     */
    fun parameters(): List<QubeCommandParameter<*>>

    /**
     * flags to control special behavior
     */
    fun flags(): Set<QubeCommandFlags>
}
