package com.rita.calendarprooo.search

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.FragmentSearchBinding
import java.io.IOException
import com.google.android.gms.maps.CameraUpdateFactory
import androidx.navigation.findNavController
import com.rita.calendarprooo.NavigationDirections
import com.rita.calendarprooo.data.Plan


class SearchFragment : Fragment(), OnMapReadyCallback {

    private val viewModel : SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    private lateinit var mMap : GoogleMap

    private val DEFAULT_ZOOM : Float = 15f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: FragmentSearchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //google map setup
        val mapFragment =  childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //search place
        fun geoLocate(){
            Log.i("Rita","geoLocate init")
            val geocoder = Geocoder(context)
            var list = mutableListOf<Address>()
            val searchText=viewModel.searchText.value.toString()
            try{
                Log.i("Rita","geoLocate success")
                list=geocoder.getFromLocationName(searchText,1)

            }
            catch (e:IOException){
                Log.e("Rita","geoLocate IOExeption: ${e.message}")
            }
            if(list.size>0){
                val address = list.get(0)
                viewModel.searchResult.value = list.get(0)
                viewModel.searchResultAddress.value=list.get(0).getAddressLine(0)
                viewModel.searchResultName.value = viewModel.searchText.value

                Log.i("Rita","geoLocate: ${viewModel.searchResult.value}")
                //Log.i("Rita","geoLocate: ${list.get(0).getAddressLine(0)}")

                moveCamera(LatLng(address.latitude,address.longitude),DEFAULT_ZOOM,
                    address.getAddressLine(0))
            }
        }


        binding.textSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if(i==EditorInfo.IME_ACTION_SEARCH||i==EditorInfo.IME_ACTION_DONE
                ||keyEvent.action==KeyEvent.ACTION_DOWN||keyEvent.action==KeyEvent.KEYCODE_ENTER){
                Log.i("Rita","setOnEditorActionListener")
                geoLocate()
                binding.textSearch.hideKeyboard()

            }
                false
        }

        //Edit page navigation
        val plan: Plan? = Plan()
        viewModel.navigateToEdit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(
                    viewModel.searchResultAddress.value, plan))
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

    fun moveCamera(latLng: LatLng, zoom:Float, title: String){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom))

        val options = MarkerOptions().position(latLng).title(title)

        mMap.addMarker(options)
    }


    fun View.hideKeyboard(){
        context?.let{
            val inputMethodManager =
                it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }



}