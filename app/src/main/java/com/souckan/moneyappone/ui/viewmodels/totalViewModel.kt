package com.souckan.moneyappone.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.souckan.moneyappone.data.database.entity.AccountEntity
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.data.repository.TotalRepository
import com.souckan.moneyappone.domain.model.BillWithDetails
import com.souckan.moneyappone.domain.model.TotalWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalViewModel @Inject constructor(
    private val repository: TotalRepository
) : ViewModel() {

    fun getAllTotals() = liveData(Dispatchers.IO) {
        emit(repository.getAllTotals())
    }

    fun getAllBillByAccount(idAccount: Int) = liveData(Dispatchers.IO) {
        emit(repository.getAllBillByAccount(idAccount))
    }

    fun insertAll(total: List<TotalEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(total)
        }
    }

    fun getCurrencyByCode(code: String) = liveData(Dispatchers.IO) {
        emit(repository.getCurrencyByCode(code))
    }

    fun insertCurrency(currency: CurrencyEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCurrency(currency)
        }
    }

    fun deleteAllTotals() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTotals()
        }
    }

    fun insertBill(
        amount: Float,
        accountName: String,
        currencyCode: String,
        billDate: String,
        description: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertBill(currencyCode, accountName, amount, billDate, description)
        }
    }

    fun getAllBills() = liveData(Dispatchers.IO) {
        emit(repository.getAllBills())
    }

    suspend fun pesosToDollar(amount: Float): Float {
        val dollarPrice = repository.getDollarPrice()
        return amount / dollarPrice
    }

    fun deleteAccountWithBills(accountId: Int) {
        viewModelScope.launch {
            repository.deleteAccountWithBills(accountId)
        }
    }

    fun updateAccountName(accountId: Int, newName: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateAccountName(accountId, newName)
    }

    fun getAllAccounts() = liveData(Dispatchers.IO) {
        emit(repository.getAllAccounts())
    }

    fun getAllAccountsNames() = liveData(Dispatchers.IO) {
        emit(repository.getAllAccountsNames())
    }

    fun getAllCurrenciesNames() = liveData(Dispatchers.IO) {
        emit(repository.getAllCurrenciesNames())
    }

    fun addAccount(accountName: String, currencyName: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.addAccount(accountName, currencyName)
    }

    fun getAllCurrencies() = liveData(Dispatchers.IO) {
        emit(repository.getAllCurrencies())
    }

    fun getBillsByAccount(idAccount: Int): LiveData<List<BillWithDetails>> {
        return repository.getBillsByAccount(idAccount)
    }

    fun getAccountNameById(idAccount: Int): LiveData<String> {
        return repository.getAccountNameById(idAccount)
    }


    val totalSumInDollars: LiveData<Double> = repository.totalSumInDollars

    fun deleteBill(billId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteBill(billId)
        }
    }

    fun getTotalNonARS(): LiveData<Float?> {
        return repository.getTotalNonARS()
    }

    fun getTotalOnlyARS(): LiveData<Float?> {
        return repository.getTotalOnlyARS()
    }

    fun getTotalOnlyBTC(): LiveData<Float?> {
        return repository.getTotalOnlyBTC()
    }

    val allBillsWithDetails: LiveData<List<BillWithDetails>> = repository.getAllBillsWithDetails()

    val totalsWithDetails: LiveData<List<TotalWithDetails>> = repository.allTotalsWithDetails

    suspend fun getDollarPrice(): Float {
        return repository.getDollarPrice()
    }

    suspend fun getBitcoinPrice(): Float {
        return repository.getBitcoinPrice()
    }


}
