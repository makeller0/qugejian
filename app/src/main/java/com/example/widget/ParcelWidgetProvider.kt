package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class ParcelWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        ParcelWidgetUpdater.updateWidgets(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        ParcelWidgetUpdater.updateWidgets(context)
    }
}
