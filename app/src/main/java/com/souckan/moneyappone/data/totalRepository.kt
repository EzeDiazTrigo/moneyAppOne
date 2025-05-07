package com.souckan.moneyappone.data.repository

import android.accounts.Account
import android.util.Log
import androidx.lifecycle.LiveData
import com.souckan.moneyappone.data.database.dao.AccountDao
import com.souckan.moneyappone.data.database.dao.BillDao
import com.souckan.moneyappone.data.database.dao.CurrencyDao
import com.souckan.moneyappone.data.database.dao.TotalDao
import com.souckan.moneyappone.data.database.entity.AccountEntity
import com.souckan.moneyappone.data.database.entity.BillEntity
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.data.network.BinanceAPIService
import com.souckan.moneyappone.data.network.NetworkModule.provideRetrofit
import com.souckan.moneyappone.domain.model.BillWithDetails
import com.souckan.moneyappone.domain.model.TotalWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class TotalRepository @Inject constructor(
    private val totalDao: TotalDao,
    private val accountDao: AccountDao,
    private val currencyDao: CurrencyDao,
    private val billDao: BillDao,
    private val apiService: BinanceAPIService
) {
    val totalSumInDollars: LiveData<Double> = totalDao.getTotalSumInDollars()

    suspend fun getAllTotals(): List<TotalEntity> {
        return totalDao.getAllTotals()
    }

    suspend fun deleteBill(billId: Int) {
        val bill = billDao.getBillById(billId)
        if (bill != null) {
            billDao.subtractFromTotal(bill.idAccount, bill.amount)
            billDao.deleteBill(bill)
        }
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

    fun getAllBillsWithDetails(): LiveData<List<BillWithDetails>> {
        return billDao.getAllBillsWithDetails()
    }

    val allTotalsWithDetails: LiveData<List<TotalWithDetails>> = totalDao.getAllTotalsWithDetails()


    suspend fun deleteAllTotals() {
        totalDao.deleteAllTotals()
    }

    suspend fun addAccount(accountName: String, currencyName: String) {
        val currency = currencyDao.getCurrencyByCode(currencyName)
        val newAccount = currency?.let {
            AccountEntity(
                accountName = accountName,
                idCurrency = it.idCurrency
            )
        }
        if (newAccount != null) {
            accountDao.insertAll(newAccount)
        }
        val currentAccount = currency?.let { accountDao.getAccountByName(accountName, it.idCurrency) }
        val newTotal =  currency?.let {
            currentAccount?.let { it1 -> TotalEntity(idCurrency = it.idCurrency, totalAmount = 0f, idAccount = it1.idAccount) }
        }
        if (newTotal != null) {
            totalDao.insertTotal(newTotal)
        }
    }

    fun getBillsByAccount(idAccount: Int): LiveData<List<BillWithDetails>> {
        return billDao.getBillsByAccount(idAccount)
    }

    suspend fun deleteAccountWithBills(accountId: Int) {
        accountDao.deleteAccountWithBills(accountId)
    }

    suspend fun updateAccountName(accountId: Int, newName: String) {
        accountDao.updateAccountName(accountId, newName)
    }

    suspend fun getCryptoPrice(symbol: String): Float {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPrice(symbol)
                response.price.toFloat()
            } catch (e: Exception) {
                e.printStackTrace()
                0f
            }
        }
    }

    suspend fun getDollarPrice(): Float {
        return getCryptoPrice("USDTARS")
    }

    suspend fun getBitcoinPrice(): Float {
        return getCryptoPrice("BTCUSDT")
    }

    suspend fun getEuroPrice(): Float {
        return getCryptoPrice("EURUSDT")
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
                        accountName = "${accountName}",
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

    suspend fun getCurrencyByCode(code: String): CurrencyEntity? {
        return currencyDao.getCurrencyByCode(code)
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


    fun getAccountNameById(idAccount: Int): LiveData<String> {
        return accountDao.getAccountNameById(idAccount)
    }

    fun getTotalOnlyARS(): LiveData<Float?> {
        return totalDao.getTotalOnlyARS("ARS")
    }

    fun getTotalOnlyBTC(): LiveData<Float?> {
        return totalDao.getTotalOnly("BTC")
    }

    fun getTotalOnlyEUR(): LiveData<Float?> {
        return totalDao.getTotalOnly("EUR")
    }

    fun getTotalNonARS(): LiveData<Float?> {
        return totalDao.getTotalNonARS("ARS")
    }

}
