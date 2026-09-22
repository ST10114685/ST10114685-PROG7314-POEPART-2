package com.example.deliberate.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SyncResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "timestamp") val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class SessionPayload(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "duration_seconds") val durationSeconds: Long,
    @Json(name = "start_time") val startTime: Long
)

@JsonClass(generateAdapter = true)
data class PracticeDataBackup(
    @Json(name = "user_email") val userEmail: String,
    @Json(name = "sessions") val sessions: List<SessionPayload>,
    @Json(name = "total_practice_minutes") val totalMinutes: Int
)
