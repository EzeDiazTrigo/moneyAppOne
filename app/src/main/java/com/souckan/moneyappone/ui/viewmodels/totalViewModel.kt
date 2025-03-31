package com.souckan.moneyappone.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.souckan.moneyappone.data.database.entity.AccountEntity
import com.souckan.moneyappone.data.database.entity.CurrencyEntity
import com.souckan.moneyappone.data.database.entity.TotalEntity
import com.souckan.moneyappone.data.repository.TotalRepository
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

    fun insertAll(total: List<TotalEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAll(total)
        }
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

    fun insertBill(amount: Float, accountName: String, currencyCode: String, billDate:String, description:String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertBill(currencyCode, accountName, amount, billDate, description)
        }
    }

    fun getAllBills() = liveData(Dispatchers.IO) {
        emit(repository.getAllBills())
    }

    suspend fun pesosToDollar(amount:Float):Float{
        val dollarPrice = repository.getDollarPrice()
        return  amount / dollarPrice
    }

    suspend fun getDollarPrice():Float{
        return repository.getDollarPrice()
    }

    fun getAllAccounts() = liveData(Dispatchers.IO) {
        emit(repository.getAllAccounts())
    }

    fun getAllAccountsNames()= liveData(Dispatchers.IO) {
        emit(repository.getAllAccountsNames())
    }

    fun getAllCurrenciesNames() = liveData(Dispatchers.IO) {
        emit(repository.getAllCurrenciesNames())
    }

    fun getAllCurrencies() = liveData(Dispatchers.IO) {
        emit(repository.getAllCurrencies())
    }

    fun getAccountNameById(id:Int) = liveData(Dispatchers.IO) {
        emit(repository.getAccountNameById(id))
    }

}
