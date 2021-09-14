package com.mena97villalobos.ltvblog.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.mena97villalobos.ltvblog.R

fun showLocationDeniedSnackBar(view: View, context: Context, action: () -> Unit) {
    val snackbar =
        Snackbar.make(view, context.getString(R.string.location_denied), Snackbar.LENGTH_LONG)
    snackbar.show()
    snackbar.addCallback(
        object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                action()
            }
        })
}

fun showRationaleAlertDialog(context: Context, onPositive: () -> Unit, onNegative: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle(R.string.permissions_rationale_title)
        .setMessage(R.string.permissions_rationale_message)
        .setPositiveButton(R.string.ok) { _, _ -> onPositive() }
        .setNegativeButton(R.string.cancel) { _, _ -> onNegative() }
        .create()
        .show()
}

fun checkFineLocationPermission(context: Context) =
    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

fun checkCoarseLocationPermission(context: Context) =
    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

/**
 * Helper function that checks [Manifest.permission.ACCESS_FINE_LOCATION] and
 * [Manifest.permission.ACCESS_COARSE_LOCATION] at the same time
 */
fun checkLocationPermission(context: Context) =
    checkFineLocationPermission(context) && checkCoarseLocationPermission(context)

/**
 * Checks if the app needs to show a rational dialog explaining the user why a permission is needed
 * @param permission: Permission to check if the rationale is needed
 */
fun shouldShowPermissionRationale(activity: Activity, permission: String): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
