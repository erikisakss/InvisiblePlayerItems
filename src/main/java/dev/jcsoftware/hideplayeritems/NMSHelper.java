package dev.jcsoftware.hideplayeritems;

import lombok.SneakyThrows;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class NMSHelper {

    @SneakyThrows
    public static void sendPacket(Player player, Object packet) {
        Object handle = getHandle(player);
        Object playerConnection = handle.getClass().getField("c").get(handle);

        playerConnection.getClass().getMethod("a", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    @SneakyThrows
    public static Object getHandle(Player player) {
        return player.getClass().getMethod("getHandle").invoke(player);
    }

    @SneakyThrows
    public static Class<?> getNMSClass(String name) {
        return Class.forName("net.minecraft.network.protocol." + name);
    }
    @SneakyThrows
    public static Class<?> getEquipmentSlotClass() {



        return net.minecraft.world.entity.EnumItemSlot.class;
    }


    @SneakyThrows
    public static Class<?> getPacketPlayoutEntityEquipment() {
        return net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment.class;
    }

    @SneakyThrows
    public static Class<?> getCraftBukkitClass(String name) {
        return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private static Method minecraftItemStack = null;
    @SneakyThrows
    public static Object toMinecraftItemStack(ItemStack itemStack) {
        if (minecraftItemStack == null) minecraftItemStack = NMSHelper.getCraftBukkitClass("inventory.CraftItemStack")
                .getMethod("asNMSCopy", ItemStack.class);
        return minecraftItemStack.invoke(null, itemStack);
    }

}
