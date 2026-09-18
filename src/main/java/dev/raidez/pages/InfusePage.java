package dev.raidez.pages;

import java.util.ArrayList;
import java.util.List;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Vector2i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import dev.raidez.GrimoirePlugin;
import dev.raidez.Utils;
import dev.raidez.resources.Infuse;
import dev.raidez.resources.Spell;

public class InfusePage extends InteractiveCustomUIPage<Infuse> {

    private static final HytaleLogger LOGGER = GrimoirePlugin.get().getLogger();
    private static final String INFUSE_PAGE_UI = "Pages/InfusePage.ui";
    private static final String SPELL_ENTRY_UI = "Pages/SpellEntry.ui";
    private static final String WHEEL_SLOT_UI = "Pages/WheelSlot.ui";

    private static final Value<String> DEFAULT_STYLE = Value.ref(WHEEL_SLOT_UI, "DefaultStyle");
    private static final Value<String> SELECTED_STYLE = Value.ref(WHEEL_SLOT_UI, "SelectedStyle");
    private static final Value<String> ERROR_STYLE = Value.ref(WHEEL_SLOT_UI, "ErrorStyle");

    private static final String SPELL_PANEL_ID = "#SpellPanel";

    private static final String SLOT_LIST_ID = "#SlotList";
    private static final String SPELL_LIST_ID = "#SpellList";

    private static final String CANCEL_BUTTON_ID = "#CancelButton";
    private static final String INFUSE_BUTTON_ID = "#InfuseButton";
    private static final String EMPTY_BUTTON_ID = "#EmptyButton";

    private final int SLOT_SIZE = 64;
    private final int SLOT_COUNT = 12;
    private final double START_ANGLE = Math.PI / 12;
    private final double STEP_ANGLE = Math.PI * 2 / SLOT_COUNT;

    private String[] slots = new String[12];
    private String[] initialSlots = new String[12];
    private int selectedSlotIndex = -1;
    private List<String> spellList = new ArrayList<>();

    public InfusePage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Infuse.CODEC);
        populateSpellList(playerRef);
    }

    public InfusePage(PlayerRef playerRef, String[] initialSlots) {
        super(playerRef, CustomPageLifetime.CanDismiss, Infuse.CODEC);
        this.initialSlots = initialSlots;
        this.slots = initialSlots.clone();
        populateSpellList(playerRef);
    }

    private void populateSpellList(PlayerRef playerRef) {
        var ref = playerRef.getReference();
        var store = playerRef.getReference().getStore();
        var scrolls = new ArrayList<String>();

        // Get all the spells from the player's inventory as scrolls
        var inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING);
        for (short slot = 0; slot < inventory.getCapacity(); slot++) {
            var is = inventory.getItemStack(slot);
            if (is == null || !Utils.isScroll(is)) {
                continue;
            }

            var spell = Spell.getFromItem(is.getItem());
            if (spell != null) {
                scrolls.add(spell.getId());
            }
        }

        this.spellList = scrolls;
    }

    @Override
    public void build(
            Ref<EntityStore> ref,
            UICommandBuilder commandBuilder,
            UIEventBuilder eventBuilder,
            Store<EntityStore> store) {

        // Append the UI page
        commandBuilder.append(INFUSE_PAGE_UI);

        // Build the wheel layout for the slots and bind their events
        buildSlotWheel(commandBuilder, eventBuilder);

        // Build the spell list for the infuse page
        buildSpellList(commandBuilder, eventBuilder);

        // Bind global events
        bindGlobalEvents(eventBuilder);
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, Infuse data) {
        switch (data.getAction()) {
            // Close the page without changes
            case Cancel -> close();
            // Open the specified slot for infusion
            case OpenSlot -> openSlot(data);
            // Update the specified slot with new data
            case UpdateSlot -> updateSlot(data);
            // Perform the infusion action
            case Infuse -> infuse();
        }
    }

    /**
     * Builds the circular slot wheel for the infuse page.
     * 
     * @param commandBuilder
     * @param eventBuilder
     */
    private void buildSlotWheel(
            UICommandBuilder commandBuilder,
            UIEventBuilder eventBuilder) {

        LOGGER.atInfo().log("Building slot wheel with %d slots.", SLOT_COUNT);

        for (int i = 0; i < SLOT_COUNT; i++) {
            // Calculate the position for the current slot in the wheel
            var slotPos = calculateSlotPositions(i, 0, 0, 500, 500, 20);

            // Create and configure the anchor for the current slot
            var anchor = new Anchor();
            anchor.setLeft(Value.of(slotPos.x));
            anchor.setTop(Value.of(slotPos.y));
            anchor.setWidth(Value.of(SLOT_SIZE));
            anchor.setHeight(Value.of(SLOT_SIZE));

            // Append the wheel slot UI to the wheel panel
            commandBuilder.append(SLOT_LIST_ID, WHEEL_SLOT_UI);
            commandBuilder.setObject(SLOT_LIST_ID + "[%s].Anchor".formatted(i), anchor);

            // Bind the event for opening the slot when the wheel slot is activated
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    SLOT_LIST_ID + "[%s]".formatted(i),
                    new EventData()
                            .append("Action", Infuse.Action.OpenSlot)
                            .append("Slot", String.valueOf(i)));

            // Set the initial item for the slot if available
            var itemId = initialSlots[i];
            if (itemId != null && !itemId.isEmpty()) {
                commandBuilder.set(SLOT_LIST_ID + "[%s] #Item.ItemId".formatted(i), itemId);
            }
        }
    }

    /**
     * Re/builds the spell list for the infuse page.
     * 
     * @param commandBuilder
     * @param eventBuilder
     */
    private void buildSpellList(
            UICommandBuilder commandBuilder,
            UIEventBuilder eventBuilder) {

        LOGGER.atInfo().log("Building spell list with %d spells.", spellList.size());

        // Clear the existing spell list before rebuilding it.
        commandBuilder.clear(SPELL_LIST_ID);

        int i = 0;
        for (String spellId : spellList) {
            var spell = Spell.getAssetMap().getAsset(spellId);

            // Append the spell entry UI to the spell list
            commandBuilder.append(SPELL_LIST_ID, SPELL_ENTRY_UI);

            // Set the UI elements for the spell entry
            var spellSelector = SPELL_LIST_ID + "[%s]".formatted(i);
            commandBuilder.set(spellSelector + " #Icon.ItemId", spell.getTexture() != null ? spell.getTexture() : "");
            commandBuilder.set(spellSelector + " #Name.Text", spell.getName());
            commandBuilder.set(spellSelector + ".TooltipText", spell.getDescription());
            commandBuilder.set(spellSelector + " #Level.Text", String.valueOf(spell.getLevel()));

            // Bind the event for updating the slot when the spell is activated
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    spellSelector,
                    new EventData()
                            .append("Action", Infuse.Action.UpdateSlot)
                            .append("ItemId", spell.getItemId())
                            .append("Slot", String.valueOf(i)));
            i++;
        }
    }

    /**
     * Binds the global events for the infuse page,
     * including remove spell, cancel, and infuse buttons.
     * 
     * @param eventBuilder
     */
    private void bindGlobalEvents(UIEventBuilder eventBuilder) {
        LOGGER.atInfo().log("Binding global events for infuse page.");

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                EMPTY_BUTTON_ID,
                new EventData()
                        .append("Action", Infuse.Action.UpdateSlot)
                        .append("ItemId", "")
                        .append("Slot", "-1"));

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                CANCEL_BUTTON_ID,
                new EventData().append("Action", Infuse.Action.Cancel));

        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                INFUSE_BUTTON_ID,
                new EventData().append("Action", Infuse.Action.Infuse));
    }

    /* Helper Methods */

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

    private void openSlot(Infuse data) {
        var commandBuilder = new UICommandBuilder();
        LOGGER.atInfo().log("Opening slot: %d", data.getSlot());

        // Reset the style of all slots to the default style before opening a new one
        for (int i = 0; i < SLOT_COUNT; i++) {
            commandBuilder.set(SLOT_LIST_ID + "[%s].Style".formatted(i), DEFAULT_STYLE);
        }

        // If the slot being opened is already the selected slot, close it and return
        if (data.getSlot() == selectedSlotIndex) {
            commandBuilder.set("#SpellPanel.Visible", false);
            sendUpdate(commandBuilder);
            return;
        }

        // Update the style of the selected slot to indicate it is open
        selectedSlotIndex = data.getSlot();
        commandBuilder.set(SLOT_LIST_ID + "[%s].Style".formatted(selectedSlotIndex), SELECTED_STYLE);

        // Show the spell panel
        commandBuilder.set(SPELL_PANEL_ID + ".Visible", true);

        sendUpdate(commandBuilder);
    }

    private void updateSlot(Infuse data) {
        var commandBuilder = new UICommandBuilder();
        var eventBuilder = new UIEventBuilder();
        var previousItemId = slots[selectedSlotIndex];
        var isRemoveAction = data.getItemId() == null || data.getItemId().isEmpty();
        var itemSelector = SLOT_LIST_ID + "[%s] #Item.ItemId".formatted(selectedSlotIndex);
        var hasPreviousItem = previousItemId != null && !previousItemId.isEmpty();

        LOGGER.atInfo().log("Updating slot: %d with item: %s (previous item: %s)",
                selectedSlotIndex,
                data.getItemId(),
                previousItemId);

        // Update the slot variable
        slots[selectedSlotIndex] = data.getItemId();

        // Reset the slot style
        commandBuilder.set(SLOT_LIST_ID + "[%s].Style".formatted(selectedSlotIndex), DEFAULT_STYLE);

        // Hide the spell panel
        commandBuilder.set(SPELL_PANEL_ID + ".Visible", false);

        // Remove the spell from the spell list
        if (data.getSlot() >= 0) {
            spellList.remove(data.getSlot());
            buildSpellList(commandBuilder, eventBuilder);
        }

        if (isRemoveAction) {
            // Set slot to null
            commandBuilder.setNull(itemSelector);

            // If there was a previous item in the slot, add it back to the spell list and
            // rebuild its entry in the UI
            if (hasPreviousItem) {
                var spell = Spell.getFromItem(Item.getAssetMap().getAsset(previousItemId));
                spellList.add(spell.getId());
                buildSpellList(commandBuilder, eventBuilder);
            }

        } else {
            // Update the item in slot
            commandBuilder.set(itemSelector, data.getItemId());
        }

        selectedSlotIndex = -1;
        sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void infuse() {
        LOGGER.atInfo().log("Infusing item in slot: %s", slots);
        close();
    }

}
