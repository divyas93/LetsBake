package com.bakingapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bakingapp.Adapter.RecipesAdapter;
import com.bakingapp.AppConstants;
import com.bakingapp.IdlingResources.SimpleIdlingResource;
import com.bakingapp.Network.RetrofitAPIInterface;
import com.bakingapp.Network.RetrofitClient;
import com.bakingapp.POJO.RecipeResults;
import com.bakingapp.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipeClickListener {

    private RetrofitAPIInterface retrofitAPIInterface;
    private RecipesAdapter recipesAdapter;
    private RecyclerView recipeRecyclerView;
    private ProgressDialog progressDialog;
    private TextView mTextView;
    private List<RecipeResults> recipeResults;
    private RecyclerView.LayoutManager layoutManager;

    private Parcelable layoutManagerSavedstate;
    private final String SAVED_LAYOUT_MANAGER = "layoutManager";

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recipeRecyclerView = (RecyclerView) findViewById(R.id.recipesRecyclerView);
        mTextView = (TextView) findViewById(R.id.noRecipeLoaded);
        retrofitAPIInterface = RetrofitClient.getRetorfitAPIInterface(AppConstants.baseURL);
        showRecipesInRecyclerView();

        getIdlingResource();

        getBakingData(mIdlingResource);
    }

    private void getBakingData(final SimpleIdlingResource simpleIdlingResource) {

        if (simpleIdlingResource != null) {
            simpleIdlingResource.setIdleState(false);
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.progressMessage));
        progressDialog.show();

        Call<ArrayList<RecipeResults>> bakingResultsCall = retrofitAPIInterface.getBakingJson();
        bakingResultsCall.enqueue(new Callback<ArrayList<RecipeResults>>() {
            @Override
            public void onResponse(Call<ArrayList<RecipeResults>> call, Response<ArrayList<RecipeResults>> response) {
                if (response.code() == 200) {
                    Log.d("BakingDataGetResponse", response.body().toString());
                    dismissProgressDialog();
                    Log.d("BakingDataGetResponse1", response.body().get(0).getName());
                    recipeResults = response.body();
                    setRecipeRecyclerViewAndDefaultTextViewVisibility(View.VISIBLE, View.GONE);
                    recipesAdapter.setRecipeData(recipeResults);
                    restoreLayoutState();
                    if (simpleIdlingResource != null) {
                        simpleIdlingResource.setIdleState(true);
                    }
                    recipesAdapter.notifyDataSetChanged();
                } else {
                    dismissProgressDialog();
                    setRecipeRecyclerViewAndDefaultTextViewVisibility(View.GONE, View.VISIBLE);
                    mTextView.setText(getString(R.string.errorString));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RecipeResults>> call, Throwable t) {
                progressDialog.dismiss();
                setRecipeRecyclerViewAndDefaultTextViewVisibility(View.GONE, View.VISIBLE);
                mTextView.setText(getString(R.string.networkErrorString));
            }
        });
    }

    private void setRecipeRecyclerViewAndDefaultTextViewVisibility(int recyclerViewVisibility, int textViewVisibility) {
        recipeRecyclerView.setVisibility(recyclerViewVisibility);
        mTextView.setVisibility(textViewVisibility);
    }

    private void showRecipesInRecyclerView() {
        if (findViewById(R.id.tabletLayout) != null) {
            int count = calculateNoOfGridColumns(this);
            layoutManager = new GridLayoutManager(this, count);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int count = calculateNoOfGridColumns(this);
                layoutManager = new GridLayoutManager(this, count);
            }

        }
        recipeRecyclerView.setLayoutManager(layoutManager);
        recipesAdapter = new RecipesAdapter(this);
        recipeRecyclerView.setAdapter(recipesAdapter);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            layoutManagerSavedstate = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, recipeRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    private void restoreLayoutState() {
        if (layoutManagerSavedstate != null) {
            recipeRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedstate);
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(AppConstants.RECIPE_DETAILS_INTENT, (Serializable) recipeResults.get(position));
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private static int calculateNoOfGridColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

}
