package dev.jcsoftware.hideplayeritems;

import com.comphenix.protocol.events.PacketEvent;
import com.mojang.datafixers.util.Pair;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class EquipmentPacketProcessor {
    protected abstract void process(PacketEvent event);
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentPacketProcessor.class);

    /**
     * Re-sends the PacketPlayOutEntityEquipment packet to all players, for this particular player
     * @param player The player to send the equipment packet on behalf of
     */
    @SneakyThrows
    public static void refreshEquipmentOfPlayerForAllPlayers(Player player) {
        List<Pair<Object, Object>> equipmentPairList = new ArrayList<>();
        Constructor<?> packetConstructor = NMSHelper.getPacketPlayoutEntityEquipment()
                .getDeclaredConstructor(int.class, List.class);

        for (ItemSlotConverter slotConverter : ItemSlotConverter.values()) {
            equipmentPairList.add(new Pair<>(
                    slotConverter.toNMSEnum(),
                    slotConverter.getMinecraftItemStack(player)
            ));
        }

        Object packet = packetConstructor.newInstance(player.getEntityId(), equipmentPairList);

        Bukkit.getOnlinePlayers().forEach(online -> {
            if (online.equals(player)) return;
            Bukkit.getLogger().info("packet: " + packet);
            NMSHelper.sendPacket(online, packet);
            LOGGER.info("Equipment of player {} has been sent to {}", player.getName(), online.getName());
        });


    }

    public static void refreshEquipmentOfAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(EquipmentPacketProcessor::refreshEquipmentOfPlayerForAllPlayers);
    }

    public void onEnable() {
        Bukkit.getLogger().info("Equipment packet processor enabled.");
        refreshEquipmentOfAllPlayers();
    }

}
