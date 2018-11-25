package com.example.drsabs.diymixer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class CreateRecipe extends AppCompatActivity implements RecyclerTouchHelper.RecyclerItemTouchHelperListener {
    private Button cancelButton;
    private Button saveButton;
    private Button mixButton;
    private FloatingActionButton addFlavorButton;
    private RecyclerView flavorRecyclerView;
    private  ArrayList<Recipe.Flavor> flavorRecyclerList = new ArrayList<Recipe.Flavor>();
    private ConstraintLayout constraintLayout;
    private FlavorAdapter fAdapter;
    private Recipe recipe;
    private EditText recipe_name;
    private EditText tot_vol;
    private EditText vg_percent;
    private EditText pg_percent;
    private EditText nic_strength;
    private EditText nic_conc;
    private Switch vgpg;
    private Boolean whoEdited = true;
    private Double totalFlavor = 0.0;
    private final TextWatcher watcherA = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable editable) {
            whoEdited = true;
            WhoEdited();
        }
    };
    private final TextWatcher watcherB = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable editable) {
            whoEdited = false;
            WhoEdited();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        final Intent intent = getIntent();
        RecyclerTouchHelper.setFromFlavorView(true);

        //Button hooks (not those kind)
        cancelButton = (Button) findViewById(R.id.cancel_button);
        saveButton = (Button) findViewById(R.id.save_button);
        mixButton = (Button) findViewById(R.id.mix_button);
        addFlavorButton = (FloatingActionButton) findViewById(R.id.add_flavor_button);
        fAdapter = new FlavorAdapter(this, flavorRecyclerList);

        //recycler hooks
        constraintLayout = findViewById(R.id.recipe_const_layout);
        flavorRecyclerView = (RecyclerView) findViewById(R.id.flavor_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        flavorRecyclerView.setLayoutManager(mLayoutManager);
        flavorRecyclerView.setItemAnimator(new DefaultItemAnimator());
        flavorRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        flavorRecyclerView.setAdapter(fAdapter);
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(flavorRecyclerView);
        prepareFlavorData();

        //Recipe field variables
        recipe_name = (EditText) findViewById(R.id.recipe_name);
        tot_vol = (EditText) findViewById(R.id.totalVolume);
        vg_percent = (EditText) findViewById(R.id.vg_percent);
        pg_percent = (EditText) findViewById(R.id.pg_percent);
        nic_strength = (EditText) findViewById(R.id.nic_strength);
        nic_conc = (EditText) findViewById(R.id.nic_conc);
        vgpg = (Switch) findViewById(R.id.vg_pg);

        //Intent editRecipe check
        final int pos = intent.getIntExtra("position", 99);
        if (pos != 99) {
            recipe = MainActivity.recipeList.get(pos);
            recipe_name.setText(recipe.getRecipeName());
            tot_vol.setText(String.valueOf(recipe.getTotVol()));
            vg_percent.setText(String.valueOf(recipe.getVgPercent()));
            pg_percent.setText(String.valueOf(recipe.getPgPercent()));
            nic_strength.setText(String.valueOf(recipe.getNicStrengh()));
            nic_conc.setText(String.valueOf(recipe.getNicConc()));
            vgpg.setChecked(recipe.getIsVg());

            //flavorRecyclerList = recipe.getFlavors();
            for(Recipe.Flavor flav : recipe.getFlavors()) {
                flavorRecyclerList.add(flav);
                prepareFlavorData();
            }
        }

        /*
        *
        * EditText Listeners
        *
         */
        vg_percent.addTextChangedListener(watcherA);
        pg_percent.addTextChangedListener(watcherB);

        /*
        *
        * Click Listeners
        *
        */
        //Button for clearing screen and going back to previous
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int childCount = flavorRecyclerView.getChildCount(), i = 0; i < childCount; ++i) {
                    final RecyclerView.ViewHolder holder = flavorRecyclerView.getChildViewHolder(flavorRecyclerView.getChildAt(i));
                fAdapter.removeItem(holder.getAdapterPosition());
                }
                flavorRecyclerList.clear();
                prepareFlavorData();
                RecyclerTouchHelper.setFromFlavorView(false);
                finish();
            }
        });


        //Button for saving recipe to recipe list
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do stuff
                if (!recipe_name.getText().toString().equals("") &&
                        !vg_percent.getText().toString().equals("") &&
                        !pg_percent.getText().toString().equals("") &&
                        !nic_strength.getText().toString().equals("") &&
                        !nic_conc.toString().equals("") &&
                        !tot_vol.getText().toString().equals("") &&
                        flavorRecyclerList.size() != 0) {
                    for(Recipe.Flavor flav : flavorRecyclerList){
                        totalFlavor += flav.getStrength();
                    }
                    if (totalFlavor <= Double.parseDouble(pg_percent.getText().toString())) {
                        totalFlavor = 0.0;
                        if (pos == 99) {
                            recipe = new Recipe(recipe_name.getText().toString(), Integer.parseInt(tot_vol.getText().toString()), Integer.parseInt(vg_percent.getText().toString()), Integer.parseInt(pg_percent.getText().toString()), Double.parseDouble(nic_strength.getText().toString()), Integer.parseInt(nic_conc.getText().toString()), vgpg.isChecked(), flavorRecyclerList, MainActivity.recipeList.size());
                            MainActivity.recipeList.add(recipe);
                        } else {
                            recipe = new Recipe(recipe_name.getText().toString(), Integer.parseInt(tot_vol.getText().toString()), Integer.parseInt(vg_percent.getText().toString()), Integer.parseInt(pg_percent.getText().toString()), Double.parseDouble(nic_strength.getText().toString()), Integer.parseInt(nic_conc.getText().toString()), vgpg.isChecked(), flavorRecyclerList, pos);
                            MainActivity.recipeList.set(pos, recipe);
                            
                        }
                        RecyclerTouchHelper.setFromFlavorView(false);
                        finish();
                    }else{
                        totalFlavor = 0.0;
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRecipe.this);
                        builder.setTitle("Flavor concentrations cannot exceed max PG concentration");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateRecipe.this);
                    builder.setTitle("Recipe information cannot be empty");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });

        //Button for accessing mixer screen
        mixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recipe_name.getText().toString().equals("") &&
                        !vg_percent.getText().toString().equals("") &&
                        !pg_percent.getText().toString().equals("") &&
                        !nic_strength.getText().toString().equals("") &&
                        !nic_conc.toString().equals("") &&
                        !tot_vol.getText().toString().equals("") &&
                        flavorRecyclerList.size() != 0) {
                    for(Recipe.Flavor flav : flavorRecyclerList){
                        totalFlavor += flav.getStrength();
                    }
                    if (totalFlavor <= Double.parseDouble(pg_percent.getText().toString())) {
                        totalFlavor = 0.0;

                        if (pos == 99){
                            recipe = new Recipe(recipe_name.getText().toString(), Integer.parseInt(tot_vol.getText().toString()), Integer.parseInt(vg_percent.getText().toString()), Integer.parseInt(pg_percent.getText().toString()), Double.parseDouble(nic_strength.getText().toString()), Integer.parseInt(nic_conc.getText().toString()), vgpg.isChecked(), (ArrayList<Recipe.Flavor>) flavorRecyclerList, MainActivity.recipeList.size());
                            MainActivity.recipeList.add(recipe);
                            int newPos = recipe.getIndex();
                            goToMix(newPos);
                        }else{
                            recipe = new Recipe(recipe_name.getText().toString(), Integer.parseInt(tot_vol.getText().toString()), Integer.parseInt(vg_percent.getText().toString()), Integer.parseInt(pg_percent.getText().toString()), Double.parseDouble(nic_strength.getText().toString()), Integer.parseInt(nic_conc.getText().toString()), vgpg.isChecked(), (ArrayList<Recipe.Flavor>) flavorRecyclerList, pos);
                            MainActivity.recipeList.set(pos, recipe);
                            goToMix(pos);
                        }

                        RecyclerTouchHelper.setFromFlavorView(false);
                        finish();
                    }else{
                        totalFlavor = 0.0;
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRecipe.this);
                        builder.setTitle("Flavor concentrations cannot exceed max PG concentration");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateRecipe.this);
                    builder.setTitle("Recipe information cannot be empty");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });
        //Button for adding a flavor
        addFlavorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateRecipe.this);
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.alert_layout, null);
                builder.setTitle("Set flavor info");
                builder.setView(alertLayout);

                final EditText name = (EditText) alertLayout.findViewById(R.id.flvname);
                final EditText str = (EditText) alertLayout.findViewById(R.id.flvstr);

                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!name.getText().toString().equals("") && !str.getText().toString().equals("")) {
                                    Recipe.Flavor flv = new Recipe.Flavor(name.getText().toString(), Double.parseDouble(str.getText().toString()));
                                    flavorRecyclerList.add(flv);
                                    prepareFlavorData();
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
    }
    //Notifies adapter of content change
    private void prepareFlavorData() {
        fAdapter.notifyDataSetChanged();
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FlavorAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = "Flavor";

            // backup of removed item for undo purpose
            final Recipe.Flavor deletedItem = flavorRecyclerList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            fAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(constraintLayout, name + " removed from flavor list", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    fAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void goToMix(int pos) {
        Intent mixScreen = new Intent(this, MixScreen.class);
        mixScreen.putExtra("mixPos", pos);
        startActivity(mixScreen);
    }
    private void WhoEdited(){
        if(whoEdited){
            if(!vg_percent.getText().toString().equals("")) {
                pg_percent.removeTextChangedListener(watcherB);
                if(Integer.parseInt(vg_percent.getText().toString()) > 100){
                    vg_percent.setText("100");
                    vg_percent.setText("0");
                }
                pg_percent.setText(String.valueOf(100 - Integer.parseInt(vg_percent.getText().toString())));
                pg_percent.addTextChangedListener(watcherB);
            }else{
                pg_percent.removeTextChangedListener(watcherB);
                vg_percent.removeTextChangedListener(watcherA);
                vg_percent.setText("0");
                pg_percent.setText("100");
                pg_percent.addTextChangedListener(watcherB);
                vg_percent.addTextChangedListener(watcherA);
            }
        }else{
            if(!pg_percent.getText().toString().equals("")) {
                vg_percent.removeTextChangedListener(watcherA);
                if(Integer.parseInt(pg_percent.getText().toString()) > 100){
                    pg_percent.setText("100");
                    vg_percent.setText("0");
                }
                vg_percent.setText(String.valueOf(100 - Integer.parseInt(pg_percent.getText().toString())));
                vg_percent.addTextChangedListener(watcherA);
            }else{
                vg_percent.removeTextChangedListener(watcherA);
                pg_percent.removeTextChangedListener(watcherB);
                pg_percent.setText("0");
                vg_percent.setText("100");
                vg_percent.addTextChangedListener(watcherA);
                pg_percent.addTextChangedListener(watcherB);
            }
        }
    }

}
