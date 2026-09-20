package com.alquran.offline.data.repository

import com.alquran.offline.model.AsmaulHusna
import kotlinx.coroutines.flow.Flow

interface AsmaulHusnaRepository {
    fun getAllAsmaulHusna(): Flow<List<AsmaulHusna>>
    suspend fun getAsmaulHusnaById(id: Int): AsmaulHusna?
    fun searchAsmaulHusna(query: String): Flow<List<AsmaulHusna>>
}
