package com.souckan.moneyappone.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
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

    fun getAllBills(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllBills()
        }
    }

    suspend fun pesosToDollar(amount:Float):Float{
        val dollarPrice = repository.getDollarPrice()
        return  amount / dollarPrice
    }
}
