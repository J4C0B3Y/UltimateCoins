package net.j4c0b3y.moneydrop.listeners.death;

import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.j4c0b3y.moneydrop.MoneyDrop;
import net.j4c0b3y.moneydrop.coins.Coins;
import net.j4c0b3y.moneydrop.party.Parties;
import net.j4c0b3y.moneydrop.utils.MythicUtils;
import net.j4c0b3y.moneydrop.utils.WorldGuardUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MobDeathListener implements Listener {

    private final MoneyDrop plugin = MoneyDrop.getInstance();
    private final YamlDocument settings = plugin.getSettings();
    private final Random random = new Random();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (entity.getType().equals(EntityType.PLAYER)) return;
        if (killer == null || !killer.getType().equals(EntityType.PLAYER)) return;
        if (plugin.isWorldGuardHook() && WorldGuardUtils.checkFlag(killer.getPlayer(), plugin.getMobFlag(), StateFlag.State.DENY)) return;

        Section section;

        if (plugin.isMythicHook() && MythicUtils.isMythicMob(entity)) {
            section = settings.getOptionalSection("death.mod.mythic." + MythicUtils.getMythicMob(entity).getMobType()).orElse(settings.getSection("death.mob.mythic.default"));
        } else {
            section = settings.getOptionalSection("death.mob.vanilla." + entity.getType().toString().toLowerCase()).orElse(settings.getSection("death.mob.vanilla.default"));
        }

        double[] raw = Arrays.stream(section.getString("amount").split("-")).mapToDouble(Double::parseDouble).toArray();
        double amount = raw.length > 1 ? random.nextDouble(raw[0], raw[1]) : raw[0];
        if (Parties.isEnabled()) amount *= Parties.getMultiplier(killer);
        List<String> coins = section.getStringList("coins");

        Coins.spawn(coins.size() > 0 ? Coins.fromPercentages(amount, coins) : Coins.fromAmount(amount), entity.getLocation(), killer, null);
    }
}
