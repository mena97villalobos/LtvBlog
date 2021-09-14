package com.mena97villalobos.ltvblog.ui.maps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
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
import com.mena97villalobos.ltvblog.utils.*

class MapsFragment : Fragment() {

    companion object {
        private const val REQUEST_LOCATION_CODE = 99
    }

    private lateinit var binding: FragmentMapsBinding
    private lateinit var map: GoogleMap
    private lateinit var currentLocationMarker: Marker
    private val bluePinBitmap by lazy {
        ResourcesCompat.getDrawable(resources, R.drawable.ic_pin_blue, null)!!.toBitmap()
    }
    private val redPinBitmap by lazy {
        ResourcesCompat.getDrawable(resources, R.drawable.ic_pin_red, null)!!.toBitmap()
    }
    private val geocoder by lazy { Geocoder(requireContext()) }
    private val fusedLocationProvider by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val locationRequest: LocationRequest =
        LocationRequest.create().apply {
            interval = 60
            fastestInterval = 60
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            maxWaitTime = 60
        }

    /**
     * Location callback to update current user position on the map
     */
    private val locationCallback: LocationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val locationList = locationResult.locations
                if (locationList.isNotEmpty() && this@MapsFragment::map.isInitialized) {
                    locationList.last().let {
                        val currentPos = LatLng(it.latitude, it.longitude)

                        // Remove last location marker, update camera position if this is the
                        // first time a marker is being added
                        if (this@MapsFragment::currentLocationMarker.isInitialized) {
                            currentLocationMarker.remove()
                        } else {
                            map.moveCamera(CameraUpdateFactory.newLatLng(currentPos))
                        }

                        // Add a marker to the map and update current location marker
                        map.addMarker(
                                MarkerOptions()
                                    .position(currentPos)
                                    .title(getString(R.string.my_position))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bluePinBitmap)))
                            ?.let { marker -> currentLocationMarker = marker }
                    }
                }
            }
        }

    private val callbackGoogleMapReady = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        checkPermissions()
        configureSearchPOI()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callbackGoogleMapReady)
    }

    /*
     * Stop listening for location updates when the lifecycle passes to the onPause state
     */
    override fun onPause() {
        super.onPause()
        if (checkLocationPermission(requireContext())) {
            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    /*
     * Start listening for location updates when the lifecycle passes to the onResume state
     */
    override fun onResume() {
        super.onResume()
        checkLocationProviderAvailability()
        if (checkLocationPermission(requireContext())) {
            setupLocationProvider()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                if (checkLocationPermission(requireContext())) {
                    setupLocationProvider()
                } else {
                    showLocationDeniedSnackBar(
                        binding.root,
                        requireContext(),
                    ) {
                        this@MapsFragment.findNavController()
                            .navigate(
                                MapsFragmentDirections.actionNavigationDashboardToNavigationHome())
                    }
                }
            }
        }
    }

    /**
     * Checks if user has the location services turned on
     * If not calls [showOpenSettingsDialog] to turn on location
     */
    private fun checkLocationProviderAvailability() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showOpenSettingsDialog()
        }
    }

    private fun checkPermissions() {
        if (!checkLocationPermission(requireContext())) {
            if (shouldShowPermissionRationale(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
                shouldShowPermissionRationale(
                    requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showRationaleAlertDialog(
                    requireContext(),
                    { requestLocationPermission() },
                    {
                        this@MapsFragment.findNavController()
                            .navigate(
                                MapsFragmentDirections.actionNavigationDashboardToNavigationHome())
                    })
            } else {
                requestLocationPermission()
            }
        } else {
            setupLocationProvider()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION_CODE)
    }

    @RequiresPermission(
        anyOf =
            [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun setupLocationProvider() {
        fusedLocationProvider?.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper())
        if (this::map.isInitialized) map.isMyLocationEnabled = true
    }

    private fun configureSearchPOI() {
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    val results = geocoder.getFromLocationName(query, 5)
                    if (results.isEmpty()) {
                        showEmptyResultsDialog()
                    } else {
                        showResultsSnackbar(results.size)
                        results.forEach {
                            val currentPos = LatLng(it.latitude, it.longitude)
                            map.addMarker(
                                MarkerOptions()
                                    .position(currentPos)
                                    .title(it.featureName)
                                    .icon(BitmapDescriptorFactory.fromBitmap(redPinBitmap))
                            )
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (this@MapsFragment::map.isInitialized &&
                        (newText == null || newText.isEmpty())) {
                        map.clear()
                    }
                    return true
                }
            })
    }

    private fun showEmptyResultsDialog() {
        AlertDialog.Builder(context)
            .setTitle(R.string.empty_results_title)
            .setMessage(R.string.empty_results_message)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showOpenSettingsDialog() {
        AlertDialog.Builder(context)
            .setTitle(R.string.location_off_title)
            .setMessage(R.string.location_off_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                this@MapsFragment.findNavController()
                    .navigate(MapsFragmentDirections.actionNavigationDashboardToNavigationHome())
            }
            .create()
            .show()
    }

    private fun showResultsSnackbar(resultsSize: Int) =
        Snackbar.make(
                binding.root,
                getString(R.string.geocoder_result, resultsSize),
                Snackbar.LENGTH_LONG)
            .show()

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireActivity())
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
