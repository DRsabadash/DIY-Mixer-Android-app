package com.example.drsabs.diymixer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MixScreen extends AppCompatActivity {
    private Recipe mixRecipe;
    private TextView recipeName;
    private TextView vgPercent;
    private TextView pgPercent;
    private TextView nicStr;
    private Switch vgpg;
    private ListView table;
    private ListView tableStr;
    private ArrayList<Recipe.Flavor> flavors;
    private Double allPG;
    private Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix);

        //Get CreateRecipe intent extra
        final Intent fromCreate = getIntent();

        //Text view and recipe hooks
        mixRecipe = MainActivity.recipeList.get(fromCreate.getIntExtra("mixPos", 99));
        table = (ListView) findViewById(R.id.ListView);
        tableStr = (ListView) findViewById(R.id.strengthView);
        recipeName = (TextView) findViewById(R.id.recipe_name);
        vgPercent = (TextView) findViewById(R.id.vg_percent);
        pgPercent = (TextView) findViewById(R.id.pg_percent);
        nicStr = (TextView) findViewById(R.id.nic_strength);
        vgpg = (Switch) findViewById(R.id.vg_pg);
        close = (Button) findViewById(R.id.Close);

        //Close button listener
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        flavors = mixRecipe.getFlavors();
        recipeName.setText(mixRecipe.getRecipeName());

        //Getting amount of nicotine base needed in ML from equation nicBaseNeeded = (targetStrength / baseConcentration in Mg per ML) * totalVolume
        Double nic = ((mixRecipe.getNicStrengh() / mixRecipe.getNicConc()) * mixRecipe.getTotVol());

        //Setting nic base volume to field
        nicStr.setText("Nic base: " + nic.toString() + " ML");

        //Setting which base to subtract nicotine volume from
        Boolean isVG = mixRecipe.getIsVg();
        Double nicBaseVG = 0.0;
        Double nicBasePG = 0.0;
        if (isVG == true) {
            nicBaseVG = nic;
        } else {
            nicBasePG = nic;
        }
        //Setting VG Percent field in % of total volume in ML
        vgPercent.setText("VG: " + String.valueOf((mixRecipe.getTotVol() * (mixRecipe.getVgPercent() * 0.01)) - nicBaseVG) + " ML");


        //Array for flavor names, Array for flavor strengths, Double for all flavors volume in ML
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> strengths = new ArrayList<String>();
        Double flavorPG = 0.0;

        //Foreach adds names to names<>, flavor strengths to strengths<>(adding a ML to the end) and all flavor volumes to flavorPG
        for (Recipe.Flavor f : mixRecipe.getFlavors()) {
            names.add(f.getName());
            flavorPG+= f.getStrength();
            Double temp = (mixRecipe.getTotVol() * (f.getStrength() * 0.01));
            strengths.add(temp.toString() + " Ml");
        }

        //Setting PG Percent field in % of total volume in ML
        pgPercent.setText("PG : " + String.valueOf(((mixRecipe.getTotVol() * (mixRecipe.getPgPercent() * 0.01)) - flavorPG) - nicBasePG) + " ML");

        //Array adapters for populating List View
        ArrayAdapter<String> nameArr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        ArrayAdapter<String> strArr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strengths);
        table.setAdapter(nameArr);
        tableStr.setAdapter(strArr);
        nameArr.notifyDataSetChanged();
        strArr.notifyDataSetChanged();
    }

}
