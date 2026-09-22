package com.example.deliberate.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PracticeSyncService {

    @POST("sync/backup")
    suspend fun uploadBackupData(
        @Header("Authorization") bearerToken: String,
        @Body backup: PracticeDataBackup
    ): SyncResponse
}
