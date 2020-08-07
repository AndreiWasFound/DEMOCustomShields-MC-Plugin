package andreiwasfound.customshields;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

    Map<String, Long> cooldowns = new HashMap<String, Long>();

    public List<String> list = new ArrayList<String>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("getblazeshield")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            if (!(sender.hasPermission("getblazeshield.use"))) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command");
                return true;
            }
            Player player = (Player) sender;
            if (player.hasPermission("getblazeshield.use")) {
                if (player.getInventory().firstEmpty() == -1) {
                    Location loc = player.getLocation();
                    World world = player.getWorld();
                    world.dropItemNaturally(loc, getBlazeShield());
                    player.sendMessage(ChatColor.GOLD + "You have recieved the Blaze Shield!");
                    return true;
                }
                player.getInventory().addItem(getBlazeShield());
                player.sendMessage(ChatColor.GOLD + "You have recieved the Blaze Shield!");
                return true;
            }
        }
        return false;
    }

    public ItemStack getBlazeShield() {
        ItemStack item = new ItemStack(Material.SHIELD);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Blaze Shield");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7(Right Click) &a&oShoot fire"));
        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.SHIELD)) {
            if (e.getPlayer().getInventory().getItemInOffHand().getItemMeta().hasLore()) {
                Player player = (Player) e.getPlayer();
                if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (player.isSneaking()) {
                        if (cooldowns.containsKey(player.getName())) {
                            if (cooldowns.get(player.getName()) > System.currentTimeMillis()) {
                                long timeleft = (cooldowns.get(player.getName()) - System.currentTimeMillis()) / 1000;
                                player.sendMessage(ChatColor.GOLD + "Ability will be ready in " + timeleft + " second(s)");
                                return;
                            }
                        }
                        cooldowns.put(player.getName(), System.currentTimeMillis() + (5 * 1000));

                        if (!list.contains(player.getName()))
                            list.add(player.getName());
                            player.launchProjectile(SmallFireball.class);
                        return;
                    }
                }
            }
            if (list.contains(e.getPlayer().getName())) {
                list.remove(e.getPlayer().getName());
            }
        }
    }
}