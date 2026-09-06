package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.ParcelDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ParcelWidgetUpdater {

    fun updateWidgets(context: Context) {
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = ParcelDatabase.getDatabase(appContext)
                val pendingList = db.parcelDao().getPendingListSync()
                val totalCount = db.parcelDao().getPendingCountSync()

                val appWidgetManager = AppWidgetManager.getInstance(appContext)
                val thisWidget = ComponentName(appContext, ParcelWidgetProvider::class.java)
                val widgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

                if (widgetIds == null || widgetIds.isEmpty()) return@launch

                for (widgetId in widgetIds) {
                    val views = RemoteViews(appContext.packageName, R.layout.widget_parcel)

                    // Click opens MainActivity
                    val intent = Intent(appContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        appContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                    if (totalCount == 0 || pendingList.isEmpty()) {
                        views.setViewVisibility(R.id.widget_empty_view, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_content_view, View.GONE)
                        views.setTextViewText(R.id.widget_badge, "0件待取")
                    } else {
                        views.setViewVisibility(R.id.widget_empty_view, View.GONE)
                        views.setViewVisibility(R.id.widget_content_view, View.VISIBLE)
                        views.setTextViewText(R.id.widget_badge, "${totalCount}件待取")

                        val first = pendingList[0]
                        views.setTextViewText(R.id.widget_code_1, first.pickupCode)
                        val stationInfo = if (first.location.isNotBlank()) {
                            "${first.courierName} · ${first.location}"
                        } else {
                            first.courierName
                        }
                        views.setTextViewText(R.id.widget_station_1, stationInfo)

                        if (pendingList.size > 1) {
                            val second = pendingList[1]
                            views.setViewVisibility(R.id.widget_second_item, View.VISIBLE)
                            views.setTextViewText(R.id.widget_code_2, second.pickupCode)
                            views.setTextViewText(R.id.widget_station_2, second.courierName)
                        } else {
                            views.setViewVisibility(R.id.widget_second_item, View.GONE)
                        }
                    }

                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
