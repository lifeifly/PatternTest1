package com.lifly.pattern.builder;

public class CompleteTerrainBuilder implements TerrainBuilder{
    private Terrain terrain=new Terrain();

    @Override
    public TerrainBuilder buildWall() {
        this.terrain.w=new Wall(0,1,2,3);
        return this;
    }

    @Override
    public TerrainBuilder buildFort() {
        this.terrain.f=new Fort(0,1,2,3);
        return this;
    }

    @Override
    public TerrainBuilder buildMine() {
        this.terrain.m=new Mine(0,1,2,3);
        return this;
    }

    @Override
    public Terrain build() {
        return this.terrain;
    }
}
