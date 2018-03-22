package com.rapidsos.utils.utils

import org.jetbrains.anko.AnkoLogger
import java.util.*

/**
 * @author Josias Sena
 */
class LanguageUtils : AnkoLogger {

    private var languageCodeMap: LinkedHashMap<String, String> = linkedMapOf()

    companion object {
        private const val SELECT_LANGUAGE = "(Language)"
    }

    init {
        buildLanguageCodeMap()
    }

    /**
     * Build [languageCodeMap] by providing all of the language codes as keys and the
     * language that they represent as values
     */
    private fun buildLanguageCodeMap() {
        languageCodeMap.put("", SELECT_LANGUAGE)
        languageCodeMap.put("en", "English")

        val isoLanguages: Array<out String> = Locale.getISOLanguages()

        isoLanguages.asSequence()
                .filter { !it.isEmpty() && it.length <= 2 }
                .filter { it != "en" }
                .onEach {
                    it.let {
                        val locale = Locale(it)
                        languageCodeMap.put(it, locale.displayLanguage)
                    }
                }
    }

    /**
     * Returns the language name for the provided language code
     *
     * For example: getLanguageCodeForLanguage("English") will return "en"
     *              getLanguageCodeForLanguage("Spanish") will return "sp"
     *
     * @param language the language to get a language code for
     * @return the language code that belongs to the language
     */
    fun getLanguageCodeForLanguage(language: String): String {
        if (languageCodeMap.values.contains(language)) {
            return languageCodeMap.filter { it.value == language }.map { it.key }.first()
        }

        return ""
    }

    /**
     * Returns the language name for the provided language code
     *
     * For example: getLanguageCodeIndex("en") will return "English"
     *              getLanguageCodeIndex("es") will return "Spanish"
     *
     * @param languageCode the language code to return the language name for
     * @return the language name for the language code
     */
    fun getLanguageForLanguageCode(languageCode: String): String {
        if (languageCodeMap.containsKey(languageCode)) {
            return languageCodeMap.filter { it.key == languageCode }.map { it.value }.first()
        }

        return ""
    }

    /**
     * Returns the index in the [languageCodeMap] for the provided language code
     *
     * @param languageCode the language code to return the language name for
     * @return the index for the language code
     */
    fun getLanguageCodeIndex(languageCode: String): Int {
        if (languageCodeMap.containsKey(languageCode)) {
            val language = languageCodeMap[languageCode]
            return getLanguagesSorted().indexOf(language)
        }

        return 0
    }

    /**
     * @return a sorted list of all languages with English being first language on the list
     */
    fun getLanguagesSorted(): List<String> {
        return languageCodeMap.values.sorted().toMutableList().apply {
            remove("English")
            add(1, "English")
        }
    }
}
