package com.example.quickresponsecode.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quickresponsecode.QuickResponseCodeApplication
import com.example.quickresponsecode.configuration.Constant
import com.example.quickresponsecode.configuration.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingDatastore
@Inject
constructor(application: QuickResponseCodeApplication) {
    // At the top level of your kotlin file:
    private val Context.datastore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = Constant.SETTING_DATASTORE)
    private val datastore = application.datastore


    // keys of datastore
    private val languageKey = stringPreferencesKey("languageKey")
    private val enableIntroKey = booleanPreferencesKey("enableIntroKey")
    private val enableLanguageIntroKey = booleanPreferencesKey("enableLanguageIntroKey")
    private val logoKey = stringPreferencesKey("logoKey")

    // Enable Intro
    var enableIntro: Boolean
        get() = runBlocking { datastore.data.first()[enableIntroKey] ?: true }
        set(value) = runBlocking { datastore.edit { pref -> pref[enableIntroKey] = value } }

    // Enable Language Intro
    var enableLanguageIntro: Boolean
        get() = runBlocking { datastore.data.first()[enableLanguageIntroKey] ?: true }
        set(value) = runBlocking { datastore.edit { pref -> pref[enableLanguageIntroKey] = value } }

    // Language
    var language: Language
        get() = Language.getByCode(
            runBlocking {
                datastore.data.first()[languageKey]
            })
        set(value) = runBlocking {
            datastore.edit { mutablePreferences ->
                mutablePreferences[languageKey] = value.code
            }
        }

    val languageFlow: Flow<Language> = datastore.data.map { mutablePreferences ->
        Language.getByCode(code = mutablePreferences[languageKey])
    }
}