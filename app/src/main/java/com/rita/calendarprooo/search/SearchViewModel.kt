package com.rita.calendarprooo.search

import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    var searchText = MutableLiveData<String?>()

    var searchResultAddress = MutableLiveData<String?>()

    var searchResult = MutableLiveData<Address?>()
    init{
        searchText.value=""
        searchResultAddress.value=null
        searchResult.value=null
    }
}