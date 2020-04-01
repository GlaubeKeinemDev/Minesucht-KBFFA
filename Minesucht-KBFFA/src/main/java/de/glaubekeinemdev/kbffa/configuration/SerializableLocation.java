package de.glaubekeinemdev.kbffa.configuration;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.beans.ConstructorProperties;

/**
 * Created by Lukas Münch on Dez 2019
 */
public class SerializableLocation {

    @NonNull
    private double x;
    @NonNull
    private double y;
    @NonNull
    private double z;
    @NonNull
    private float yaw;
    @NonNull
    private float pitch;
    @NonNull
    private String world;

    @ConstructorProperties({"x", "y", "z", "yaw", "pitch", "world"})
    private SerializableLocation( @NonNull double x, @NonNull double y, @NonNull double z, @NonNull float yaw, @NonNull float pitch, @NonNull String world)
    {
        if (world == null) {
            throw new NullPointerException("world");
        }
        this.x = x;this.y = y;this.z = z;this.yaw = yaw;this.pitch = pitch;this.world = world;
    }

    public Location toLocation()
    {
        return new Location( Bukkit.getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public static SerializableLocation toLocation(Location location)
    {
        return new SerializableLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld().getName());
    }

    public SerializableLocation() {}

}
