package languageSupport

import java.util.*

class LanguageControl : ResourceBundle.Control() {
    override fun getCandidateLocales(baseName: String, locale: Locale): List<Locale> {
        return listOf(Locale(locale.language), Locale.ROOT)
    }
}

class Localization(baseName: String) {
    private val control = LanguageControl()
    private val defaultBundle: ResourceBundle = ResourceBundle.getBundle(baseName, Locale.ENGLISH, control)
    private val bundle: ResourceBundle = ResourceBundle.getBundle(baseName, Locale.getDefault(), control)

    fun getString(key: LocalizationKey): String {
        return try {
            bundle.getString(key.name)
        } catch (e: MissingResourceException) {
            defaultBundle.getString(key.name)
        }
    }
}