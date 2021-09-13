package com.mena97villalobos.ltvblog.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.mena97villalobos.ltvblog.R

fun showLocationDeniedSnackBar(
    view: View,
    context: Context,
    action: () -> Unit
) {
    val snackbar = Snackbar.make(
        view,
        context.getString(R.string.location_denied),
        Snackbar.LENGTH_LONG
    )
    snackbar.show()
    snackbar.addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            action()
        }
    })
}

fun showRationaleAlertDialog(
    context: Context,
    onPositive: () -> Unit,
    onNegative: () -> Unit
) {
    AlertDialog.Builder(context)
        .setTitle(R.string.permissions_rationale_title)
        .setMessage(R.string.permissions_rationale_message)
        .setPositiveButton(R.string.ok) { _, _ -> onPositive() }
        .setNegativeButton(R.string.cancel) { _, _ -> onNegative() }
        .create()
        .show()
}