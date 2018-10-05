package com.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bakingapp.AppConstants;
import com.bakingapp.POJO.RecipeIngredients;
import com.bakingapp.ui.MainActivity;

import java.util.List;

public class WidgetUpdateService extends IntentService
{
    private RecipeIngredients[]mIngredients;

    public WidgetUpdateService()
    {
        super("UpdateWidgetService");
    }

    private String recipeName;

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        if (intent != null && intent.getAction().equals(AppConstants.WIDGET_UPDATE_ACTION))
        {
            Bundle bundle = intent.getBundleExtra(AppConstants.BUNDLE);
            List<RecipeIngredients> recipeIngredients = (List<RecipeIngredients>) bundle.getSerializable(AppConstants.INGREDIENTS_INTENT);
            if (recipeIngredients != null)
            {
                mIngredients = new RecipeIngredients[recipeIngredients.size()];
                for (int i = 0; i < recipeIngredients.size(); i++)
                {
                    mIngredients[i] = (RecipeIngredients) recipeIngredients.get(i);
                }
            }

            if(intent.hasExtra(AppConstants.INGREDIENTS_NAME)) {
                recipeName = intent.getStringExtra(AppConstants.INGREDIENTS_NAME);
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidgetProvider.class));
            BakingAppWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetIds,mIngredients, recipeName);
        }
    }
}
