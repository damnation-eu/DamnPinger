package eu.damnation.pinger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.Sound;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;

public class DamnPinger extends JavaPlugin implements Listener  {
    Pattern pattern;
    HashMap<String,java.util.UUID> lookup = new HashMap<String,java.util.UUID>();

    public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChatEvent(AsyncPlayerChatEvent event) {
        if(pattern == null) {
			lookup.clear();
			StringBuilder sb = new StringBuilder("\\b(");
			for(Player player: this.getServer().getOnlinePlayers()){
				sb.append(Pattern.quote(player.getDisplayName()));
				sb.append("|");
				lookup.put(player.getDisplayName().toLowerCase(),player.getUniqueId());
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")\\b");
            pattern = Pattern.compile(sb.toString(),Pattern.CASE_INSENSITIVE);
        }
        Matcher matcher = pattern.matcher(event.getMessage());
        if(matcher.find()){
			final java.util.UUID uuid = lookup.get(matcher.group(1).toLowerCase());
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					Player player = DamnPinger.this.getServer().getPlayer(uuid);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
				}
			});
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		pattern = null;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
		pattern = null;
    }
}
