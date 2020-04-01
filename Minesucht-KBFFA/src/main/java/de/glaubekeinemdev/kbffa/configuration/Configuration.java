package de.glaubekeinemdev.kbffa.configuration;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lukas Münch (GlaubeKeinemDev) on Mär 2020
 */
@Getter
public class Configuration {

    private HashMap< String, MapData > maps;

    public Configuration( ) {
        this.maps = new HashMap<>();
    }

    public void save( final File file ) throws IOException {
        final FileWriter fileWriter = new FileWriter( file );
        fileWriter.write( new GsonBuilder().setPrettyPrinting().create().toJson( this ) );
        fileWriter.flush();
        fileWriter.close();
    }
}
