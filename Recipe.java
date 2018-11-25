package com.example.drsabs.diymixer;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe {
    // Recipe name information
    private String recipeName;
    //Recipe volume information
    private int totVol;
    private int vgPercent;
    private int pgPercent;
    private double nicStrengh;
    private int nicConc;
    private boolean isVG = true;
    //Flavor information
    private ArrayList<Flavor> flavors;
    //Index
    private int index;

    //class constructor
    public Recipe(String recipeName, int totVol, int vgPercent, int pgPercent, double nicStrengh, int nicConc, boolean isVG, ArrayList<Flavor> flavors, int index) {
        this.recipeName = recipeName;
        this.totVol = totVol;
        this.vgPercent = vgPercent;
        this.pgPercent = pgPercent;
        this.nicStrengh = nicStrengh;
        this.nicConc = nicConc;
        this.isVG = isVG;
        this.flavors = flavors;
        this.index = index;
    }

    //Getters
    public String getRecipeName() {
        return recipeName;
    }

    public int getTotVol() {
        return totVol;
    }

    public int getVgPercent() {
        return vgPercent;
    }

    public int getPgPercent() {
        return pgPercent;
    }

    public double getNicStrengh() {
        return nicStrengh;
    }

    public int getNicConc() {
        return nicConc;
    }

    public boolean getIsVg() {
        return isVG;
    }


    public ArrayList<Flavor> getFlavors() {
        return flavors;
    }

    public int getIndex() {
        return index;
    }

    //Setters
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setTotVol(int totVol) {
        this.totVol = totVol;
    }

    public void setVgPercent(int vgPercent) {
        this.vgPercent = vgPercent;
    }

    public void setPgPercent(int pgPercent) {
        this.pgPercent = pgPercent;
    }

    public void setNicStrengh(double nicStrengh) {
        this.nicStrengh = nicStrengh;
    }

    public void setNicConc(int nicConc) {
        this.nicConc = nicConc;
    }

    public void setIsVG(boolean VG) {
        isVG = VG;
    }

    public void setFlavors(int position, String name, Double concentration) {
        this.flavors.set(position, new Flavor(name, concentration));
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public static class Flavor {
        private String name;
        private double conc;

        Flavor(String name, Double conc) {
            this.name = name;
            this.conc = conc;
        }

        public String getName() {
            return this.name;
        }

        public Double getStrength() {
            return this.conc;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setConc(double conc) {
            this.conc = conc;
        }
    }
}
