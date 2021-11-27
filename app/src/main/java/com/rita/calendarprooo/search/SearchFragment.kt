package com.rita.calendarprooo.search

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rita.calendarprooo.NavigationDirections
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.FragmentSearchBinding


class SearchFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private lateinit var mMap: GoogleMap
    private val DEFAULT_ZOOM: Float = 15f
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentSearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // Google map setup
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment

        mapFragment.getMapAsync(this)


        // Google places API
        // Initialize the SDK
        Places.initialize(requireContext(), getString(R.string.google_key))


        binding.textSearch.setOnClickListener {

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireContext())

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        }


        // Edit page navigation
        val plan = Plan()
        viewModel.navigateToEdit.observe(viewLifecycleOwner, {
            it?.let {
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(
                        viewModel.searchResultAddress.value, plan
                    )
                )
                viewModel.doneNavigated()
            }
        })


        return binding.root


    }

    override fun onMapReady(map: GoogleMap) {

        mMap = map

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        val options = MarkerOptions().position(latLng).title(title)
        mMap.addMarker(options)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place: ${place.name}, ${place.latLng}, ${place.address}")

                        viewModel.searchResultAddress.value = place.address
                        viewModel.searchResultName.value = place.name
                        viewModel.searchText.value = place.name

                        Log.i(TAG, "Place: ${viewModel.searchResultAddress.value}," +
                                    "${viewModel.searchResultName.value}, " +
                                    "${viewModel.searchText.value}")

                        moveCamera(place.latLng, DEFAULT_ZOOM, place.address)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}