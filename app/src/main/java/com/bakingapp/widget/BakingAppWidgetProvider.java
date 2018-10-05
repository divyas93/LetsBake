package com.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bakingapp.POJO.RecipeIngredients;
import com.bakingapp.R;

public class BakingAppWidgetProvider extends AppWidgetProvider {
    public static RecipeIngredients[] mIngredients;
    public static String recipeName;

    public BakingAppWidgetProvider() {

    }

    /**
     * method will update the ListView each time the user opens the IngredientsFragment for any recipe,
     *
     * @param context          app context
     * @param appWidgetManager app WidgetManger
     * @param appWidgetIds     ids which will be updated
     * @param ingredients      the ingredients of the last viewed recipe
     * @param recipeName       - displays the name of recipe on widget
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetIds[], RecipeIngredients[] ingredients, String recipeName) {
        mIngredients = ingredients;
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, ListViewService.class);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget_provider);
            views.setRemoteAdapter(R.id.list_view_widget, intent);
            views.setTextViewText(R.id.widgetRecipeName, recipeName);
            ComponentName component = new ComponentName(context, BakingAppWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view_widget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetRecipeName);
            appWidgetManager.updateAppWidget(component, views);
        }

    }

    /**
     * the widget will update itself each time the IngredientFragment will open
     *
     * @param context          app context
     * @param appWidgetManager the application WidgetManager
     * @param appWidgetIds     ids which will be updated
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }
}

