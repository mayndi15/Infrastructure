package exceptions

import java.util.Properties

object I18N {
    private val messages: MutableMap<String, Properties> = mutableMapOf()

    init {
        loadLanguage("pt_BR")
        loadLanguage("en_US")
    }

    private fun loadLanguage(language: String) {
        val classLoader = this::class.java.classLoader
        val properties = Properties()
        classLoader.getResourceAsStream("language_${language}.properties").use { inputStream ->
            properties.load(inputStream)
        }
    }

    fun getString(key: String, language: String, vararg args: Any): String {
        val properties = messages[language] ?: messages["en_US"]
        val message = properties?.getProperty(key) ?: key
        return message.format(*args)
    }
}