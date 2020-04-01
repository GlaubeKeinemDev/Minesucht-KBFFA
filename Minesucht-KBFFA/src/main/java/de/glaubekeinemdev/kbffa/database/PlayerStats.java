package de.glaubekeinemdev.kbffa.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@AllArgsConstructor
@Setter
public class PlayerStats {

    private double kills;
    private double deaths;

    public void addKill() {
        kills = kills + 1;
    }

    public void addDeath() {
        deaths = deaths + 1;
    }

    public int getKills( ) {
        return (int) kills;
    }

    public int getDeaths( ) {
        return (int) deaths;
    }

    public double getKillsDouble( ) {
        return kills;
    }

    public double getDeathsDouble( ) {
        return deaths;
    }

    public double getKD() {
        if( kills == 0 )
            return 0.00;

        if(deaths == 0)
            return kills / 1;

        final DecimalFormat decimalFormat = new DecimalFormat( "#.##" );
        final double kd = kills / deaths;

        return Double.parseDouble( decimalFormat.format( kd ) );
    }

}
