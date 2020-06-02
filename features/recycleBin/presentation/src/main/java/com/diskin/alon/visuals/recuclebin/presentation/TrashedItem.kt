package com.diskin.alon.visuals.recuclebin.presentation

import android.net.Uri

/**
 * Data holder for info about item that has been moved by app user
 * to app recycle bin.
 */
data class TrashedItem(val uri: Uri, val type: TrashedItemType)