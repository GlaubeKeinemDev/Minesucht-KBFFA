package de.glaubekeinemdev.kbffa.configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
@Setter
public class MapData {

    private String mapName;

    private Double deathHeight;
    private Double protectionHeight;

    private SerializableLocation spawnLocation;

    public MapData( String mapName ) {
        this.mapName = mapName;
    }

    public MapData( String mapName, Double deathHeight, Double protectionHeight, SerializableLocation spawnLocation ) {
        this.mapName = mapName;
        this.deathHeight = deathHeight;
        this.protectionHeight = protectionHeight;
        this.spawnLocation = spawnLocation;
    }
}
