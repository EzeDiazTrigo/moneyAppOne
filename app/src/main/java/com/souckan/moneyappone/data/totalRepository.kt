package com.souckan.moneyappone.data.repository

import com.souckan.moneyappone.data.database.dao.AccountDao
import com.souckan.moneyappone.data.database.dao.BillDao
import com.souckan.moneyappone.data.database.dao.CurrencyDao
import com.souckan.moneyappone.data.database.dao.TotalDao
import com.souckan.moneyappone.data.database.entity.AccountEntity
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.data.network.DollarAPIService
import com.souckan.moneyappone.data.network.NetworkModule.provideRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TotalRepository @Inject constructor(
    private val totalDao: TotalDao,
    private val accountDao: AccountDao,
    private val currencyDao: CurrencyDao,
    private val billDao: BillDao
) {

    suspend fun getAllTotals(): List<TotalEntity> {
        return totalDao.getAllTotals()
    }

    suspend fun insertAll(total: List<TotalEntity>) {
        totalDao.insertAll(total)
    }

    suspend fun deleteAllTotals() {
        totalDao.deleteAllTotals()
    }

    //Llamar APIs para obtenerlo
    suspend fun getDollarPrice():Float{
        return withContext(Dispatchers.IO) {
            val call = provideRetrofit().create(DollarAPIService::class.java).getDollarPrice("blue")
            val response = call.body()
            if (call.isSuccessful && response != null) {
                response.venta.toFloat()
            } else {
                1.0F
            }
        }
    }



    suspend fun insertBill(currencyCode: String, accountName: String,amount: Float, billDate: String, description:String) {
        // Verificar si la cuenta existe, si no, crearla
        var account = accountDao.getAccountByName(accountName)
        if (account == null) {
            val newAccount = AccountEntity(accountName = accountName, currecies = listOf(currencyCode))
            accountDao.insertAll(newAccount)
            //account = accountDao.getAccountByName(accountName) // Obtener el nuevo ID
        }

        // Verificar si la moneda existe, si no, crearla
        var currency = currencyDao.getCurrencyByCode(currencyCode)
        if (currency == null) {
            val newCurrency = CurrencyEntity(currencyName = currencyCode, dollarPrice = getDollarPrice())
            currencyDao.insertAll(newCurrency)
            //currency = currencyDao.getCurrencyByCode(currencyCode) // Obtener el nuevo ID
        }

        // Insertar el nuevo Bill
        val bill = BillEntity(currencyCode = currencyCode, accountName = accountName, amount = amount, billDate = billDate, description = description)
        billDao.insertAll(bill)

        // Verificar si ya existe un total para esta cuenta y moneda
        var total = totalDao.getTotalByAccountAndCurrency(accountName, currencyCode)
        if (total == null) {
            totalDao.updateTotal(TotalEntity(currency = currencyCode,  totalAmount = amount, account = accountName))
        }

        // Actualizar el total con el nuevo monto
        total.totalAmount += amount
        totalDao.updateTotal(total)
    }
}
