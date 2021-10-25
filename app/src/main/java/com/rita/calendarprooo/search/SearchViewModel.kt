package com.rita.calendarprooo.search

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    var searchText = MutableLiveData<String?>()

    var searchResultName = MutableLiveData<String?>()

    var searchResultAddress = MutableLiveData<String?>()

    var searchResult = MutableLiveData<Address?>()

    private var _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit : LiveData<Boolean>
        get() = _navigateToEdit


    init{
        searchText.value=""
        searchResultAddress.value=null
        searchResult.value=null
    }

    fun startNavigateToEdit(){
        _navigateToEdit.value=true
    }

    fun doneNavigated(){
        _navigateToEdit.value=null
    }
}