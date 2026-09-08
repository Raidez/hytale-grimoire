package dev.raidez.pages;

import java.util.ArrayList;
import java.util.List;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.Vector2i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.resources.Infuse;

public class InfusePage extends InteractiveCustomUIPage<Infuse> {

    private final int SLOT_SIZE = 64;
    private final int SLOT_COUNT = 12;
    private final int PICKER_SLOT_COUNT = 19;

    private final Vector2i PICKER_SIZE = new Vector2i(64 * 5 + 20, 64 * 2 + 20);
    private final Vector2i PICKER_PADDING = new Vector2i(640, 330);

    private final double START_ANGLE = Math.PI / 12;
    private final double STEP_ANGLE = Math.PI * 2 / SLOT_COUNT;

    private List<String> slots;
    private List<String> pickerSlots;
    private List<Vector2i> slotPositions;
    private int lastSlot = -1;
    private int rowCount = 1;

    public InfusePage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Infuse.CODEC);
        slots = List.of("", "", "", "Scroll_Fireball", "", "", "", "", "", "", "", "");
        slotPositions = new ArrayList<>();
        pickerSlots = new ArrayList<>();
    }

    public InfusePage(PlayerRef playerRef, List<String> initialSlots) {
        super(playerRef, CustomPageLifetime.CanDismiss, Infuse.CODEC);
        slots = initialSlots;
        slotPositions = new ArrayList<>();
        pickerSlots = new ArrayList<>();
    }

    @Override
    public void build(
            Ref<EntityStore> ref,
            UICommandBuilder commandBuilder,
            UIEventBuilder eventBuilder,
            Store<EntityStore> store) {

        // Append the UI page
        commandBuilder.append("Pages/InfusePage.ui");

        // Build the wheel layout for the slots and bind their events
        for (int i = 0; i < SLOT_COUNT; i++) {
            var slotPos = calculateSlotPositions(i, 0, 0, 500, 500, 20);
            slotPositions.add(slotPos);

            var anchor = new Anchor();
            anchor.setLeft(Value.of(slotPos.x));
            anchor.setTop(Value.of(slotPos.y));
            anchor.setWidth(Value.of(SLOT_SIZE));
            anchor.setHeight(Value.of(SLOT_SIZE));

            commandBuilder.setObject("#Slot" + i + ".Anchor", anchor);

            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#Slot" + i,
                    new EventData()
                            .append("Action", Infuse.Action.Picker)
                            .append("Slot", String.valueOf(i)));
        }

        // Set initial items for the slots based on the slots list
        for (int i = 0; i < slots.size(); i++) {
            var itemId = slots.get(i);
            if (!itemId.isEmpty()) {
                setItemSlot(i, itemId, commandBuilder);
            }
        }

        // Bind events
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CancelButton",
                new EventData().append("Action", Infuse.Action.Cancel));
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#InfuseButton",
                new EventData().append("Action", Infuse.Action.Infuse));
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ClosePicker",
                new EventData()
                        .append("Action", Infuse.Action.Slot)
                        .append("ItemId", ""));

        // Handle picker slots for the scroll items
        var scrollItems = getAllScrollItem(ref, store);
        for (int i = 0; i < Math.min(PICKER_SLOT_COUNT, scrollItems.size()); i++) {
            var item = scrollItems.get(i);

            commandBuilder.set("#Pick" + i + " #Item.ItemId", item);

            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#Pick" + i,
                    new EventData()
                            .append("Action", Infuse.Action.Slot)
                            .append("ItemId", item));

            pickerSlots.add(item);
        }

        if (scrollItems.size() > 4) {
            commandBuilder.set("#Row1.Visible", true);
            rowCount++;
        }
        if (scrollItems.size() > 9) {
            commandBuilder.set("#Row2.Visible", true);
            rowCount++;
        }
        if (scrollItems.size() > 14) {
            commandBuilder.set("#Row3.Visible", true);
            rowCount++;
        }
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, Infuse data) {
        switch (data.getAction()) {
            case Cancel -> close();
            case Picker -> openPicker(data);
            case Slot -> updateSlot(data);
            case Infuse -> {
                // Handle the infuse action here
            }
        }
    }

    /**
     * Calculates the top-left position of a slot in a circular layout
     * based on its index, the bounding box of the circle, and the margin.
     * 
     * @param slotIndex
     * @param left
     * @param top
     * @param width
     * @param height
     * @param margin
     * @return
     */
    private Vector2i calculateSlotPositions(
            int slotIndex,
            int left,
            int top,
            int width,
            int height,
            int margin) {

        // Calculate the center of the bounding box
        int centerX = left + width / 2;
        int centerY = top + height / 2;

        // Determine the radius of the circular layout based on the smallest side of the
        // bounding box and the margin
        int smallestSide = Math.min(width, height);
        int radius = smallestSide / 2 - SLOT_SIZE / 2 - margin;

        // Calculate the angle for the current slot
        double angleRadians = START_ANGLE + slotIndex * STEP_ANGLE;

        // Calculate the position from an angle and radius
        double slotCenterX = centerX + radius * Math.sin(angleRadians);
        double slotCenterY = centerY - radius * Math.cos(angleRadians);

        // Padding to get the top-left corner of the slot
        int slotLeft = (int) Math.round(slotCenterX - SLOT_SIZE / 2.0);
        int slotTop = (int) Math.round(slotCenterY - SLOT_SIZE / 2.0);

        return new Vector2i(slotLeft, slotTop);
    }

    private void setItemSlot(int slotIndex, String itemId) {
        var commandBuilder = new UICommandBuilder();
        commandBuilder.set("#Slot" + slotIndex + " #Item.ItemId", itemId);
        sendUpdate(commandBuilder);
    }

    private void setItemSlot(int slotIndex, String itemId, UICommandBuilder commandBuilder) {
        commandBuilder.set("#Slot" + slotIndex + " #Item.ItemId", itemId);
    }

    private void openPicker(Infuse data) {
        // Calculate picker position
        var slotPos = slotPositions.get(data.getSlot());
        var anchor = new Anchor();
        anchor.setLeft(Value.of(slotPos.x + PICKER_PADDING.x));
        anchor.setTop(Value.of(slotPos.y + PICKER_PADDING.y));
        anchor.setWidth(Value.of(PICKER_SIZE.x));
        anchor.setHeight(Value.of(64 * rowCount + 20));

        // Set picker position and make it visible
        var commandBuilder = new UICommandBuilder();
        commandBuilder.setObject("#Picker.Anchor", anchor);
        commandBuilder.set("#Picker.Visible", true);
        sendUpdate(commandBuilder);

        lastSlot = data.getSlot();
    }

    private void closePicker() {
        var commandBuilder = new UICommandBuilder();
        commandBuilder.set("#Picker.Visible", false);
        sendUpdate(commandBuilder);
    }

    private void updateSlot(Infuse data) {
        // Handle the slot action here
        int slot = lastSlot;
        setItemSlot(slot, data.getItemId());
        closePicker();
    }

    private List<String> getAllScrollItem(Ref<EntityStore> ref, Store<EntityStore> store) {
        var found = new ArrayList<String>();

        var inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING);
        for (short i = 0; i < inventory.getCapacity(); i++) {
            var is = inventory.getItemStack(i);
            if (is == null || is.isEmpty()) {
                continue;
            }

            // Check if the item has scroll tag
            var item = is.getItem();
            var tagIndex = AssetRegistry.getOrCreateTagIndex("Scroll");
            var tags = item.getData().getTags();
            if (!tags.containsKey(tagIndex)) {
                continue;
            }

            found.add(item.getId());
        }

        return found;
    }

}
