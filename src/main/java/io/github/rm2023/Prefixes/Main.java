package io.github.rm2023.Prefixes;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
   
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().addPermission(new Permission("prefixes.set", "Allows a player to set their own prefix."));
        this.getCommand("prefix").setExecutor(new PrefixCommand());
    }

    private class PrefixCommand implements CommandExecutor {
        private static final int PRIORITY = 69;
        private static final int LIMIT = 25;

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You can't do this!");
                return true;
            }
            if (!sender.hasPermission("prefixes.set")) {
                sender.sendMessage(ChatColor.RED + "You can't do this!");
                return true;
            }
            String prefix = String.join(" ", args);
            if (prefix.length() > LIMIT) {
                sender.sendMessage(ChatColor.RED + "Please enter a string that's shorter than " + LIMIT + " characters.");
                return true;
            }
            if (prefix.toLowerCase().contains("owner")) {
                sender.sendMessage(ChatColor.RED + "Abuse of this command will lead to its revocation.");
                return true;
            }
            User p = LuckPermsProvider.get().getUserManager().getUser(((Player) sender).getUniqueId());
            p.getNodes().stream().filter(NodeType.PREFIX::matches).filter((node) -> (((PrefixNode) node).getPriority() == PRIORITY)).forEach(node -> p.data().remove(node));
            if (!(prefix.equals("clear") || prefix.equals(""))) {
                if (!p.data().add(PrefixNode.builder(prefix.concat(ChatColor.RESET + " "), PRIORITY).build()).wasSuccessful()) {
                    sender.sendMessage(ChatColor.RED + "An error occured.");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Your prefix has been set.");
            }
            else {
                sender.sendMessage(ChatColor.GREEN + "Your prefix has been cleared.");
            }
            LuckPermsProvider.get().getUserManager().saveUser(p);
            return true;
        }

    }
}

