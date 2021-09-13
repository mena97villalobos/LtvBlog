package com.mena97villalobos.ltvblog.ui.maps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.mena97villalobos.ltvblog.R
import com.mena97villalobos.ltvblog.databinding.FragmentMapsBinding


class MapsFragment : Fragment() {

    companion object {
        private const val REQUEST_LOCATION_CODE = 99
    }

    private lateinit var binding: FragmentMapsBinding
    private lateinit var map: GoogleMap
    private lateinit var currentLocationMarker: Marker
    private val geocoder by lazy {
        Geocoder(requireContext())
    }

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 60
        fastestInterval = 60
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermission()
        configureSearchPOI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun showLocationDeniedSnackBar() {
        val snackbar = Snackbar.make(
            binding.root,
            "You need to allow location access to use this functionality!",
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                this@MapsFragment.findNavController()
                    .navigate(MapsFragmentDirections.actionNavigationDashboardToNavigationHome())
            }
        })
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.isMyLocationEnabled = true
    }

    private fun fineLocationGranted() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun coarseLocationGranted() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty() && this@MapsFragment::map.isInitialized) {
                locationList.last().let {
                    val currentPos = LatLng(it.latitude, it.longitude)
                    if (this@MapsFragment::currentLocationMarker.isInitialized) {
                        currentLocationMarker.remove()
                    }
                    map.addMarker(
                        MarkerOptions()
                            .position(currentPos)
                            .title("My Position")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )?.let { marker ->
                        currentLocationMarker = marker
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLng(currentPos))
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (fineLocationGranted() && coarseLocationGranted()) {
            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    private fun shouldShowPermissionRationale(permission: String): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)

    private fun checkLocationPermission() {
        if (!fineLocationGranted() || !coarseLocationGranted()) {
            if (shouldShowPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                showRationaleAlertDialog()
            } else {
                requestLocationPermission()
            }
        } else {
            onPermissionGranted()
        }
    }

    private fun showRationaleAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the Location permission, please accept to use location functionality")
            .setPositiveButton(
                "OK"
            ) { _, _ ->
                //Prompt the user once explanation has been shown
                requestLocationPermission()
            }
            .setNegativeButton("Cancel") { _, _ ->
                this@MapsFragment.findNavController()
                    .navigate(MapsFragmentDirections.actionNavigationDashboardToNavigationHome())
            }
            .create()
            .show()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                if (fineLocationGranted() && coarseLocationGranted()) {
                    onPermissionGranted()
                } else {
                    showLocationDeniedSnackBar()
                }
            }
        }
    }

    private fun onPermissionGranted() {
        fusedLocationProvider?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun configureSearchPOI() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                val results = geocoder.getFromLocationName(query, 5)
                showResultsSnackbar(results.size)
                results.forEach {
                    val currentPos = LatLng(it.latitude, it.longitude)
                    map.addMarker(MarkerOptions().position(currentPos).title(it.featureName))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (this@MapsFragment::map.isInitialized && (newText == null || newText.isEmpty())) {
                    map.clear()
                }
                return true
            }

        })
    }

    private fun showResultsSnackbar(resultsSize: Int) =
        Snackbar.make(
            binding.root,
            resources.getQuantityString(R.plurals.geocoder_results, resultsSize),
            Snackbar.LENGTH_LONG
        ).show()

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireActivity())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}