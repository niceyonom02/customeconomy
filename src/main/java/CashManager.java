import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CashManager implements CommandExecutor {
    private HashMap<UUID, Long> credentialMap;

    public void loadMap(){
        HashMap<UUID, Long> map = new HashMap<UUID, Long>();
        if(CustomEconomy.customEconomy.getConfig().getConfigurationSection("credential") != null){
            for(String key : CustomEconomy.customEconomy.getConfig().getConfigurationSection("credential").getKeys(false)){
                UUID uuid = UUID.fromString(key);
                Long money = CustomEconomy.customEconomy.getConfig().getLong("credential." + key);

                map.put(uuid, money);
            }
        }

        credentialMap = map;
    }

    public void save(){
        CustomEconomy.customEconomy.getConfig().set("credential", null);

        for(UUID uuid : credentialMap.keySet()){
            Bukkit.getLogger().info(uuid + "");
            CustomEconomy.customEconomy.getConfig().set("credential." + uuid.toString(), credentialMap.get(uuid));
        }
        CustomEconomy.customEconomy.saveConfig();
        credentialMap.clear();
    }



    public void deposit(UUID uuid, long money){
        if(credentialMap.containsKey(uuid)){
            credentialMap.put(uuid, credentialMap.get(uuid) + money);
        } else{
            credentialMap.put(uuid, money);
        }
    }

    public void showCurrentMoney(Player player){
        long money;
        if(credentialMap.containsKey(player.getUniqueId())){
            money = credentialMap.get(player.getUniqueId());
        } else{
            money = 0;
        }

        player.sendMessage(ChatColor.YELLOW + "현재 보유 금액은 " + money + "N입니다.");
    }

    public boolean hasBalance(UUID uuid, long balance){
        if(credentialMap.containsKey(uuid)){
            return credentialMap.get(uuid) >= balance;
        }
        return false;
    }

    public boolean withdraw(UUID uuid, long money){
        if(credentialMap.containsKey(uuid)){
            if(credentialMap.get(uuid) >= money){
                credentialMap.put(uuid, credentialMap.get(uuid) - money);
                return true;
            } else{
                return false;
            }
        } else{
            return false;
        }
    }

    public void send(UUID sender, UUID receiver, long money){
        if(withdraw(sender, money)){
            deposit(receiver, money);

            Bukkit.getPlayer(sender).sendMessage(Bukkit.getPlayer(receiver).getName() + "님에게 " + money + "N를 전송하였습니다.");
            Bukkit.getPlayer(receiver).sendMessage(Bukkit.getPlayer(sender).getName() + "님으로부터 " + money + "N를 전송받았습니다!");
        } else{
            Bukkit.getPlayer(sender).sendMessage("돈이 부족합니다!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if(label.equalsIgnoreCase("돈")){
            if(args.length > 0){
                switch (args[0]){
                    /**case "순위":
                     case "랭킹":
                     showRank(player);
                     break;*/
                    case "디버그":
                        if(player.isOp()){
                            credentialMap.put(player.getUniqueId(), 100000L) ;
                        }
                        break;
                    case "보내기":
                        if(args.length > 2){
                            if(args[1].equalsIgnoreCase(player.getName())){
                                player.sendMessage("자기 자신에게는 송금할 수 없습니다!");
                                return false;
                            }

                            if(Bukkit.getPlayer(args[1]) != null){
                                try
                                {
                                    long money = Long.parseLong(args[2]);
                                    if(money > 0){
                                        send(player.getUniqueId(), Bukkit.getPlayer(args[1]).getUniqueId(), money);
                                        return true;
                                    } else{
                                        player.sendMessage("0 초과의 정수만 입력 가능합니다!");
                                    }
                                }
                                catch(NumberFormatException e)
                                {
                                    player.sendMessage("0 초과의 정수만 입력 가능합니다!");
                                    return false;
                                }
                            } else{
                                player.sendMessage("해당 플레이어가 접속중이 아닙니다!");
                                return false;
                            }
                        } else{

                        }

                }
            } else{
                showCurrentMoney(player);
                return true;
            }
        }
        return false;
    }
}
