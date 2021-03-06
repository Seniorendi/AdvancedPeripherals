package de.srendi.advancedperipherals.common.events;

import com.google.common.collect.EvictingQueue;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.ARGogglesItem;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.network.MNetwork;
import de.srendi.advancedperipherals.network.messages.ClearHudCanvasMessage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    private static final int CHAT_QUEUE_MAX_SIZE = 50;
    public static final EvictingQueue<Pair<Long, ChatMessageObject>> messageQueue = EvictingQueue.create(CHAT_QUEUE_MAX_SIZE);
    public static long counter = 0;

    @SubscribeEvent
    public static void onChatBox(ServerChatEvent event) {
        if (AdvancedPeripheralsConfig.enableChatBox) {
            String message = event.getMessage();
            boolean isHidden = false;
            if (message.startsWith("$")) {
                event.setCanceled(true);
                message = message.replace("$", "");
                isHidden = true;
            }
            messageQueue.add(Pair.of(counter, new ChatMessageObject(event.getUsername(), message,
                    event.getPlayer().getUUID().toString(), isHidden)));
            counter++;
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        if (!(livingEntity instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
        if (event.getFrom().getItem() instanceof ARGogglesItem) {
            MNetwork.sendTo(new ClearHudCanvasMessage(), player);
        }
    }

    public static long traverseChatMessages(long lastConsumedMessage, Consumer<ChatMessageObject> consumer) {
        for (Pair<Long, ChatMessageObject> message : messageQueue) {
            if (message.getLeft() <= lastConsumedMessage)
                continue;
            consumer.accept(message.getRight());
            lastConsumedMessage = message.getLeft();
        }
        return lastConsumedMessage;
    }

    public static class ChatMessageObject {

        public String username;
        public String message;
        public String uuid;
        public boolean isHidden;

        public ChatMessageObject(String username, String message, String uuid, boolean isHidden) {
            this.username = username;
            this.message = message;
            this.uuid = uuid;
            this.isHidden = isHidden;
        }
    }
}
