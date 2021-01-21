import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.UUID;

public class CustomEconomy extends JavaPlugin implements CommandExecutor {
    public static CustomEconomy customEconomy;
    private CashManager cashManager;

    @Override
    public void onEnable(){
        registerConfig();
        customEconomy = this;
        cashManager = new CashManager();
        cashManager.loadMap();
        getCommand("돈").setExecutor(cashManager);
    }

    public CashManager getCashManager(){
        return cashManager;
    }

    private void registerConfig(){
        File f = new File(getDataFolder(), File.separator + "config.yml");
        if(!f.exists()) {
            saveResource("config.yml", false);
        }
    }

    @Override
    public void onDisable(){
        cashManager.save();
    }



}
