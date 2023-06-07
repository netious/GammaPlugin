package pl.netious.gammaplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GammaPlugin extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private Set<UUID> gammaPlayers;

    @Override
    public void onEnable() {
        getLogger().info("GammaPlugin został włączony!");
        loadConfig();
        gammaPlayers = new HashSet<>();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("GammaPlugin został wyłączony!");
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        config = getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            gammaPlayers.add(player.getUniqueId());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("gamma")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (gammaPlayers.contains(player.getUniqueId())) {
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    gammaPlayers.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Zdejmujesz efekt gamma!");
                } else {
                    player.addPotionEffect(getGammaEffect());
                    gammaPlayers.add(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "Otrzymujesz efekt gamma!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Komendę można wywołać tylko jako gracz!");
            }
            return true;
        }
        return false;
    }

    private PotionEffect getGammaEffect() {
        int duration = config.getInt("effect.duration", Integer.MAX_VALUE);
        int amplifier = config.getInt("effect.amplifier", 0);
        boolean ambient = config.getBoolean("effect.ambient", true);
        boolean particles = config.getBoolean("effect.particles", false);

        return new PotionEffect(PotionEffectType.NIGHT_VISION, duration, amplifier, ambient, particles);
    }
}
