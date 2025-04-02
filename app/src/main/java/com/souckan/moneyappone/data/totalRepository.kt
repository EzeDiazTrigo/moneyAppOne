package com.souckan.moneyappone.data.repository

import androidx.lifecycle.LiveData
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
    val totalSumInDollars: LiveData<Double> = totalDao.getTotalSumInDollars()

    suspend fun getAllTotals(): List<TotalEntity> {
        return totalDao.getAllTotals()
    }

    suspend fun deleteBill(billId: Int) {
        billDao.deleteBill(billId)
    }

    suspend fun getAllBillByAccount(idAccount: Int): List<BillEntity> {
        return billDao.getAllBillByAccount(idAccount)
    }

    suspend fun insertAll(total: List<TotalEntity>) {
        totalDao.insertAll(total)
    }

    suspend fun insertCurrency(currency: CurrencyEntity) {
        var currencyName = currencyDao.getCurrencyByCode(currency.currencyName)
        if (currencyName == null) {
            currencyDao.insertAll(currency)
        }
        if (currencyName != null) {
            if (currencyName.currencyName == "ARS") {
                currencyName.dollarPrice = currency.dollarPrice
                currencyDao.insertAll(currencyName)
            }
        }

    }


    suspend fun deleteAllTotals() {
        totalDao.deleteAllTotals()
    }

    //Llamar APIs para obtenerlo
    suspend fun getDollarPrice(): Float {
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


    suspend fun insertBill(
        currencyCode: String,
        accountName: String,
        amount: Float,
        billDate: String,
        description: String
    ) {
        // Verificar si la cuenta existe, si no, crearla
        var currency = currencyDao.getCurrencyByCode(currencyCode)
        var account = currency?.let { accountDao.getAccountByName(accountName, it.idCurrency) }
        var currencyAccount = currencyDao.getCurrencyByCode(currencyCode)?.idCurrency
        if (account == null) {
            val newAccount =
                currencyAccount?.let {
                    AccountEntity(
                        accountName = "${accountName} (${currencyCode})",
                        idCurrency = it
                    )
                }
            if (newAccount != null) {
                accountDao.insertAll(newAccount)
            }
            if (currency != null) {
                account = accountDao.getAccountByName(accountName, currency.idCurrency)
            } // Obtener el nuevo ID
        }

        // Verificar si la moneda existe, si no, crearla

        var currentDollarPrice: Float = 1.0F
        if (currency == null) {
            when (currencyCode) {
                "USD" -> {
                    currentDollarPrice = 1.0F
                }

                "ARG" -> {
                    currentDollarPrice = getDollarPrice()
                }
            }
            val newCurrency =
                CurrencyEntity(currencyName = currencyCode, dollarPrice = currentDollarPrice)
            currencyDao.insertAll(newCurrency)
            currency = currencyDao.getCurrencyByCode(currencyCode) // Obtener el nuevo ID
        }

        // Insertar el nuevo Bill
        val bill = account?.let {
            currency?.idCurrency?.let { it1 ->
                BillEntity(
                    idCurrency = it1,
                    idAccount = it.idAccount,
                    amount = amount,
                    billDate = billDate,
                    description = description
                )
            }
        }
        if (bill != null) {
            billDao.insertAll(bill)
        }

        // Verificar si ya existe un total para esta cuenta y moneda
        var total = account?.let {
            currency?.let { it1 ->
                totalDao.getTotalByAccountAndCurrency(
                    it.idAccount,
                    it1.idCurrency
                )
            }
        }
        if (total == null) {
            if (account != null) {
                if (currency != null) {
                    totalDao.updateTotal(
                        TotalEntity(
                            idCurrency = currency.idCurrency,
                            totalAmount = amount,
                            idAccount = account.idAccount
                        )
                    )
                }
            }
        }

        // Actualizar el total con el nuevo monto
        if (total != null) {
            total.totalAmount = total.totalAmount + amount
        }
        if (total != null) {
            totalDao.updateTotal(total)
        }
    }

    suspend fun getAllBills(): List<BillEntity> {
        return billDao.getAllBills()
    }

    suspend fun getAllAccounts(): List<AccountEntity> {
        return accountDao.getAllAccount()
    }

    suspend fun getAllCurrencies(): List<CurrencyEntity> {
        return currencyDao.getAllCurrencies()
    }

    suspend fun getAllAccountsNames(): List<String> {
        return accountDao.getAllAccountNames()
    }

    suspend fun getAllCurrenciesNames(): List<String> {
        return currencyDao.getAllCurrenciesNames()
    }

    suspend fun getAccountNameById(id: Int): AccountEntity {
        return accountDao.getAccountNameById(id)
    }


}
