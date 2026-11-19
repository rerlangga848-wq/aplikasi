package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.npaDataStore: DataStore<Preferences> by preferencesDataStore(name = "npa_prefs")

data class PersistedNpaState(
    val selectedChallengeId: String? = null,
    val pledgerName: String = "",
    val pledgeDate: String = "",
    val isPledged: Boolean = false,
    val bestDetoxSeconds: Int = 0,
    val totalDetoxSeconds: Int = 0,
    val detoxSessionsCount: Int = 0,
    val lastDoomSeconds: Int? = null,
    val lastDoomHabitScore: Int? = null,
    val lastDoomAvgSpeedDpPerSec: Int? = null
)

class NpaPreferencesRepository(private val context: Context) {

    private object Keys {
        val CHALLENGE_ID = stringPreferencesKey("challenge_id")
        val PLEDGER_NAME = stringPreferencesKey("pledger_name")
        val PLEDGE_DATE = stringPreferencesKey("pledge_date")
        val IS_PLEDGED = booleanPreferencesKey("is_pledged")
        val BEST_DETOX_SEC = intPreferencesKey("best_detox_sec")
        val TOTAL_DETOX_SEC = intPreferencesKey("total_detox_sec")
        val DETOX_SESSIONS = intPreferencesKey("detox_sessions")
        val LAST_DOOM_SEC = intPreferencesKey("last_doom_sec")
        val LAST_DOOM_HABIT_SCORE = intPreferencesKey("last_doom_habit_score")
        val LAST_DOOM_AVG_SPEED = intPreferencesKey("last_doom_avg_speed")
    }

    val stateFlow: Flow<PersistedNpaState> = context.npaDataStore.data.map { prefs ->
        PersistedNpaState(
            selectedChallengeId = prefs[Keys.CHALLENGE_ID],
            pledgerName = prefs[Keys.PLEDGER_NAME] ?: "",
            pledgeDate = prefs[Keys.PLEDGE_DATE] ?: "",
            isPledged = prefs[Keys.IS_PLEDGED] ?: false,
            bestDetoxSeconds = prefs[Keys.BEST_DETOX_SEC] ?: 0,
            totalDetoxSeconds = prefs[Keys.TOTAL_DETOX_SEC] ?: 0,
            detoxSessionsCount = prefs[Keys.DETOX_SESSIONS] ?: 0,
            lastDoomSeconds = prefs[Keys.LAST_DOOM_SEC],
            lastDoomHabitScore = prefs[Keys.LAST_DOOM_HABIT_SCORE],
            lastDoomAvgSpeedDpPerSec = prefs[Keys.LAST_DOOM_AVG_SPEED]
        )
    }

    suspend fun savePledge(challengeId: String, name: String, date: String, pledged: Boolean) {
        context.npaDataStore.edit { prefs ->
            prefs[Keys.CHALLENGE_ID] = challengeId
            prefs[Keys.PLEDGER_NAME] = name
            prefs[Keys.PLEDGE_DATE] = date
            prefs[Keys.IS_PLEDGED] = pledged
        }
    }

    suspend fun clearPledge() {
        context.npaDataStore.edit { prefs ->
            prefs.remove(Keys.CHALLENGE_ID)
            prefs.remove(Keys.PLEDGER_NAME)
            prefs.remove(Keys.PLEDGE_DATE)
            prefs[Keys.IS_PLEDGED] = false
        }
    }

    suspend fun recordDetoxSession(seconds: Int) {
        if (seconds <= 0) return
        context.npaDataStore.edit { prefs ->
            val prevBest = prefs[Keys.BEST_DETOX_SEC] ?: 0
            val prevTotal = prefs[Keys.TOTAL_DETOX_SEC] ?: 0
            val prevCount = prefs[Keys.DETOX_SESSIONS] ?: 0
            if (seconds > prevBest) prefs[Keys.BEST_DETOX_SEC] = seconds
            prefs[Keys.TOTAL_DETOX_SEC] = prevTotal + seconds
            prefs[Keys.DETOX_SESSIONS] = prevCount + 1
        }
    }

    suspend fun saveDoomResult(seconds: Int, habitScore: Int, avgSpeedDpPerSec: Int) {
        context.npaDataStore.edit { prefs ->
            prefs[Keys.LAST_DOOM_SEC] = seconds
            prefs[Keys.LAST_DOOM_HABIT_SCORE] = habitScore
            prefs[Keys.LAST_DOOM_AVG_SPEED] = avgSpeedDpPerSec
        }
    }
}
