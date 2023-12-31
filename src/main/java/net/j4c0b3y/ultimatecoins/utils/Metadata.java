package net.j4c0b3y.ultimatecoins.utils;

import lombok.experimental.UtilityClass;
import net.j4c0b3y.ultimatecoins.UltimateCoins;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.Optional;

@UtilityClass
public class Metadata {
    private static final UltimateCoins plugin = UltimateCoins.getInstance();

    public Optional<Object> get(Metadatable object, String key) {
        for (MetadataValue value : object.getMetadata(key)) {
            if (value.getOwningPlugin() != plugin) continue;
            return Optional.ofNullable(value.value());
        }

        return Optional.empty();
    }

    public boolean has(Metadatable object, String key) {
        return get(object, key).isPresent();
    }

    public void set(Metadatable object, String key, Object value) {
        object.setMetadata(key, new FixedMetadataValue(plugin, value));
    }
}
