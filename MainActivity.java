package com.example.drsabs.diymixer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static ArrayList<Recipe> recipeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipeAdapter rAdapter;
    private CoordinatorLayout coordinatorLayout;
    private boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Shared preferences access for recipe arraylist
        //object deserialized by Gson
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("RecipeArray", null);
        Type type = new TypeToken<ArrayList<Recipe>>(){}.getType();
        recipeList= gson.fromJson(json, type);

        //Empty toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Action button for add recipe
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.AddButton);

        //Recycler View implementation
        recyclerView = findViewById(R.id.recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        rAdapter = new RecipeAdapter(this, recipeList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(rAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //populate the Recycler View
        prepareRecipeData();

        //OnClick / OnTouch listeners for button or entry objects
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Recipe recipe = recipeList.get(position);
                Toast.makeText(getApplicationContext(), recipe.getRecipeName() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent editRecipe = new Intent(MainActivity.this, CreateRecipe.class);
                editRecipe.putExtra("position", recipe.getIndex());
                startActivity(editRecipe);
            }

            //OnClick for press and hold functionality
            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "WHEOOOO!", Toast.LENGTH_SHORT).show();
            }
        }));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewRecipe();

            }
        });
    }

    //Called populate new recipe entry in recycler view (on refocus activity)
    //fixes bug where sometimes recipes do not show up in recycler when new recipe saved.
    @Override
    public  void onResume(){
        super.onResume();
        prepareRecipeData();
    }

    //Populating the recycler view
    //Saves current recipe list every time recycler is populated
    public void prepareRecipeData() {
        saveInstance();
        rAdapter.notifyDataSetChanged();
    }

    //Called when recyclerview entry is swiped RIGHT to LEFT
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RecipeAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = recipeList.get(viewHolder.getAdapterPosition()).getRecipeName();

            // backup of removed item for undo purpose
            final Recipe deletedItem = recipeList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            rAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from recipe list", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    rAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
        prepareRecipeData();
    }

    //Called when new recipe button is pressed. creates new intent
    private void CreateNewRecipe() {
        Intent newRecipe = new Intent(this, CreateRecipe.class);
        newRecipe.putExtra("position", 99);
        startActivity(newRecipe);
    }

    //Called to save ArrayList of recipes to SharedPreferences
    //Complex object parsed to String by Gson & serialized
    public void saveInstance(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipeList);
        prefsEditor.putString("RecipeArray", json);
        prefsEditor.commit();

    }


    /***
     * Saved for eventual menu options

     Saved for eventual menu option
     @Override public boolean onCreateOptionsMenu(Menu menu) {
     // Inflate the menu; this adds items to the action bar if it is present.
     getMenuInflater().inflate(R.menu.menu_main, menu);
     return true;
     }

     @Override public boolean onOptionsItemSelected(MenuItem item) {
     // Handle action bar item clicks here. The action bar will
     // automatically handle clicks on the Home/Up button, so long
     // as you specify a parent activity in AndroidManifest.xml.
     int id = item.getItemId();

     //noinspection SimplifiableIfStatement
     if (id == R.id.action_settings) {
     return true;
     }

     return super.onOptionsItemSelected(item);
     }
     */
}
