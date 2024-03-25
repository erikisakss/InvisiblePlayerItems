package dev.jcsoftware.hideplayeritems.processor;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.datafixers.util.Pair;
import dev.jcsoftware.hideplayeritems.EquipmentPacketProcessor;
import dev.jcsoftware.hideplayeritems.NMSHelper;
import net.minecraft.world.entity.EnumItemSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeldEquipmentPacketProcessor extends EquipmentPacketProcessor {

    private Object handSlotEnum = null;
    private Object offhandSlotEnum = null;

    public HeldEquipmentPacketProcessor() {
        handSlotEnum = EnumItemSlot.a("mainhand");
        offhandSlotEnum = EnumItemSlot.a("offhand");
    }

    public void process(PacketEvent event) {
        Object rawPacket = event.getPacket().getHandle();

        try {
            Bukkit.getLogger().info(("Packet: " + rawPacket.getClass().getName()));
            Field pairField = rawPacket.getClass().getDeclaredField("c");
            pairField.setAccessible(true);

            List<Pair<Object, Object>> newPairList = new ArrayList<>();
            List<Pair<Object, Object>> existingPairList = (List<Pair<Object, Object>>) pairField.get(rawPacket);

            for (Pair<Object, Object> pair : existingPairList) {
                Object itemSlotObject = pair.getFirst();

                Pair<Object, Object> replaced = pair;

                if (Objects.equals(handSlotEnum, itemSlotObject) ||
                        Objects.equals(offhandSlotEnum, itemSlotObject)) {
                    replaced = new Pair<>(
                            itemSlotObject,
                            NMSHelper.toMinecraftItemStack(new ItemStack(Material.AIR))
                    );
                }

                newPairList.add(replaced);
            }

            pairField.set(rawPacket, newPairList);
            pairField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        event.setPacket(PacketContainer.fromPacket(rawPacket));
    }
}
