package com.rita.calendarprooo.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    val searchText = MutableLiveData<String?>()

    val searchResultName = MutableLiveData<String?>()

    val searchResultAddress = MutableLiveData<String?>()

    private val _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit: LiveData<Boolean>
        get() = _navigateToEdit


    init {
        searchText.value = ""
        searchResultAddress.value = null
    }


    fun startNavigateToEdit() {
        _navigateToEdit.value = true
    }

    fun doneNavigated() {
        _navigateToEdit.value = null
    }
}