package com.example.quickresponsecode.data.repository

import com.example.quickresponsecode.configuration.Language
import com.example.quickresponsecode.data.datastore.SettingDatastore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository
@Inject
constructor(
    private val settingDatastore: SettingDatastore,
) {
    // ENABLE INTRO
    fun enableIntro(): Boolean {
        return settingDatastore.enableIntro
    }

    fun setEnableIntro(boolean: Boolean) {
        settingDatastore.enableIntro = boolean
    }

    // ENABLE LANGUAGE INTRO
    fun enableLanguageIntro(): Boolean {
        return settingDatastore.enableLanguageIntro
    }

    fun setEnableLanguageIntro(boolean: Boolean) {
        settingDatastore.enableLanguageIntro = boolean
    }


    // LANGUAGE
    fun getLanguage(): Language {
        return settingDatastore.language
    }

    fun setLanguage(language: Language) {
        settingDatastore.language = language
    }

    fun getLanguageFlow() = settingDatastore.languageFlow

    fun enableRationaleDialog(): Boolean = settingDatastore.enableRationaleDialog
    fun disableRationaleDialog() {
        settingDatastore.enableRationaleDialog = false
    }
}