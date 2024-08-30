package com.rankdeliveryplugin.ordercode;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import java.util.HashSet;
import java.util.Set;

public class OrderCodePlugin extends JavaPlugin {

    private LuckPerms luckPerms;
    private Set<String> validCodes;

    @Override
    public void onEnable() {
        // Register LuckPerms API
        luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        if (luckPerms == null) {
            getLogger().severe("LuckPerms not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }

        // Initialize the set of valid codes
        validCodes = new HashSet<>();
        initializeCodes();

        getLogger().info("OrderCodePlugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("OrderCodePlugin has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("order")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    String code = args[0];
                    if (validCodes.contains(code)) {
                        grantVipRank(player);
                        player.sendMessage("§aCongratulations! You have successfully redeemed the VIP rank.");
                        getServer().broadcastMessage(player.getName() + " has redeemed a VIP rank!");

                        // Remove the code after it is used so it cannot be reused
                        validCodes.remove(code);

                    } else {
                        player.sendMessage("§cInvalid code! Please try again.");
                    }
                } else {
                    player.sendMessage("§cUsage: /order <12-digit-code>");
                }
            } else {
                sender.sendMessage("Only players can use this command.");
            }
            return true;
        }
        return false;
    }

    private void grantVipRank(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            Node vipNode = Node.builder("group.vip").build();
            user.data().add(vipNode);
            luckPerms.getUserManager().saveUser(user);
        } else {
            getLogger().severe("Could not find LuckPerms user for " + player.getName());
        }
    }

    private void initializeCodes() {
        // Add 12-character alphanumeric codes here 
        validCodes.add("example00000")
    }
}
