package com.souckan.moneyappone.data.repository

import com.souckan.moneyappone.data.database.dao.TotalDao
import com.souckan.moneyappone.data.database.entity.TotalEntity
import javax.inject.Inject

class TotalRepository @Inject constructor(private val totalDao: TotalDao) {

    suspend fun getAllTotals(): List<TotalEntity> {
        return totalDao.getAllTotals()
    }

    suspend fun insertAll(total: List<TotalEntity>) {
        totalDao.insertAll(total)
    }

    suspend fun deleteAllTotals() {
        totalDao.deleteAllTotals()
    }
}
