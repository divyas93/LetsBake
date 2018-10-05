package com.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.bakingapp.AppConstants;
import com.bakingapp.POJO.RecipeIngredients;
import com.bakingapp.POJO.RecipeSteps;
import com.bakingapp.R;
import com.bakingapp.widget.WidgetUpdateService;

import java.io.Serializable;
import java.util.List;

public class RecipeIngredientsAndStepsActivity extends AppCompatActivity implements RecipeStepsFragment.OnFragmentInteractionListener, RecipeIngredientsFragment.OnFragmentInteractionListener {

    private List<RecipeIngredients> recipeIngredients;
    private List<RecipeSteps> recipeSteps;
    private int recipeStepPosition;
    private String recipeName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_ingredients_and_steps);

        if (getIntent().hasExtra(AppConstants.INGREDIENTS_NAME)) {
            recipeName = getIntent().getStringExtra(AppConstants.INGREDIENTS_NAME);
        }

        if (getIntent().hasExtra(AppConstants.INGREDIENTS_INTENT)) {
            recipeIngredients = (List<RecipeIngredients>) getIntent().getSerializableExtra(AppConstants.INGREDIENTS_INTENT);
            RecipeIngredientsFragment recipeIngredientsFragment = RecipeIngredientsFragment.newInstance(recipeIngredients);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipeIngredientsContainer, recipeIngredientsFragment)
                    .commit();

            startWidgetService();
        }

        if (getIntent().hasExtra(AppConstants.STEPS_INTENT)) {
            recipeSteps = (List<RecipeSteps>) getIntent().getSerializableExtra(AppConstants.STEPS_INTENT);
            recipeStepPosition = getIntent().getIntExtra(AppConstants.STEPS_INTENT_POSITION, 0);
            RecipeStepsFragment recipeStepsFragment = RecipeStepsFragment.newInstance(recipeSteps.get(recipeStepPosition));

            if (recipeStepPosition + 1 == recipeSteps.size()) {
                recipeStepsFragment.setHideNextButtonVisibility(true);
            } else {
                recipeStepsFragment.setHideNextButtonVisibility(false);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipeIngredientsContainer, recipeStepsFragment)
                    .commit();
        }
    }

    @Override
    public void setActionBar(String appTitle) {
        getSupportActionBar().setTitle(appTitle);
    }

    //For non tablet device
    @Override
    public void onPreviousClicked(int stepId) {
        if (stepId >= 1 && stepId < recipeSteps.size()) {

            RecipeStepsFragment recipeStepsFragment = RecipeStepsFragment.newInstance(recipeSteps.get(stepId - 1));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipeIngredientsContainer, recipeStepsFragment)
                    .commit();
        }
    }

    //For non tablet device
    @Override
    public boolean onNextClicked(int stepId) {
        if (stepId >= 0 && stepId < recipeSteps.size()) {

            RecipeStepsFragment recipeStepsFragment = RecipeStepsFragment.newInstance(recipeSteps.get(stepId + 1));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.recipeIngredientsContainer, recipeStepsFragment)
                    .commit();
        }

        if (((stepId + 2) >= recipeSteps.size())) {
            return true;
        } else return false;
    }

    void startWidgetService() {
        Intent i = new Intent(this, WidgetUpdateService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.INGREDIENTS_INTENT, (Serializable) recipeIngredients);
        if (!recipeName.equalsIgnoreCase("")) {
            i.putExtra(AppConstants.INGREDIENTS_NAME, recipeName);
        }
        i.putExtra(AppConstants.BUNDLE, bundle);
        i.setAction(AppConstants.WIDGET_UPDATE_ACTION);
        startService(i);
    }

}
