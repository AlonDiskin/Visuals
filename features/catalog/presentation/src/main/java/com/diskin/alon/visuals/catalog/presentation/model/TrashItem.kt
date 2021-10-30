package com.diskin.alon.visuals.catalog.presentation.model

import android.net.Uri

/**
 * Data holder for info about item that has been moved by app user
 * to app recycle bin.
 */
data class TrashItem(val uri: Uri, val type: TrashItemType)