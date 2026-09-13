package dev.raidez.pages;

import java.util.ArrayList;
import java.util.List;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
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

import dev.raidez.GrimoirePlugin;
import dev.raidez.Utils;
import dev.raidez.resources.GrimoireMetadata;
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

    private final int SLOT_SIZE = 64;
    private final int SLOT_COUNT = 12;
    private final double START_ANGLE = Math.PI / 12;
    private final double STEP_ANGLE = Math.PI * 2 / SLOT_COUNT;

    private List<String> initialSlots = new ArrayList<>();
    private int selectedSlotIndex = -1;
    private List<Spell> spellList = new ArrayList<>();

    public InfusePage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Infuse.CODEC);
        populateInitialSlots(playerRef);
        populateSpellList(playerRef);
    }

    public InfusePage(PlayerRef playerRef, List<String> initialSlots) {
        super(playerRef, CustomPageLifetime.CanDismiss, Infuse.CODEC);
        this.initialSlots = initialSlots;
        populateSpellList(playerRef);
    }

    private void populateInitialSlots(PlayerRef playerRef) {
        var ref = playerRef.getReference();
        var store = playerRef.getReference().getStore();

        var is = InventoryComponent.getItemInHand(store, ref);
        if (Utils.isGrimoire(is)) {
            // Get the grimoire metadata from the item in hand
            var grimoire = is.getFromMetadataOrDefault(GrimoireMetadata.KEY,
                    GrimoireMetadata.CODEC);
            this.initialSlots = grimoire.getScrollList();
        }
    }

    private void populateSpellList(PlayerRef playerRef) {
        var ref = playerRef.getReference();
        var store = playerRef.getReference().getStore();
        var scrolls = new ArrayList<Spell>();

        // Get all the spells from the player's inventory as scrolls
        var inventory = InventoryComponent.getCombined(store, ref, InventoryComponent.EVERYTHING);
        for (short slot = 0; slot < inventory.getCapacity(); slot++) {
            var is = inventory.getItemStack(slot);
            if (is == null || !Utils.isScroll(is)) {
                continue;
            }

            var spell = Spell.getFromItem(is.getItem());
            if (spell != null) {
                scrolls.add(spell);
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
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CancelButton",
                new EventData().append("Action", Infuse.Action.Cancel));
        eventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#InfuseButton",
                new EventData().append("Action", Infuse.Action.Infuse));
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, Infuse data) {
        switch (data.getAction()) {
            case Cancel -> close();
            case OpenSlot -> openSlot(data);
            case UpdateSlot -> updateSlot(data);
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

        LOGGER.atInfo().log("Building slot wheel with " + SLOT_COUNT + " slots.");

        for (int i = 0; i < SLOT_COUNT; i++) {
            var slotPos = calculateSlotPositions(i, 0, 0, 500, 500, 20);

            var anchor = new Anchor();
            anchor.setLeft(Value.of(slotPos.x));
            anchor.setTop(Value.of(slotPos.y));
            anchor.setWidth(Value.of(SLOT_SIZE));
            anchor.setHeight(Value.of(SLOT_SIZE));

            // Append the wheel slot UI to the wheel panel
            commandBuilder.append("#WheelPanel", WHEEL_SLOT_UI);
            commandBuilder.setObject("#WheelPanel[%s].Anchor".formatted(i), anchor);

            // Bind the slot to the corresponding event
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#WheelPanel[%s]".formatted(i),
                    new EventData()
                            .append("Action", Infuse.Action.OpenSlot)
                            .append("Slot", String.valueOf(i)));

            // Set the initial item for the slot if available
            if (initialSlots.size() > i) {
                var itemId = initialSlots.get(i);
                if (!itemId.isEmpty()) {
                    commandBuilder.set("#WheelPanel[%s] #Item.ItemId".formatted(i), itemId);
                }
            }
        }
    }

    /**
     * Builds the spell list for the infuse page.
     * 
     * @param commandBuilder
     * @param eventBuilder
     */
    private void buildSpellList(
            UICommandBuilder commandBuilder,
            UIEventBuilder eventBuilder) {

        LOGGER.atInfo().log("Building spell list with " + spellList.size() + " spells.");

        for (int i = 0; i < spellList.size(); i++) {
            var spell = spellList.get(i);

            commandBuilder.append("#SpellPanel", SPELL_ENTRY_UI);
            commandBuilder.set("#SpellPanel[%s] #Icon.ItemId".formatted(i),
                    spell.getTexture() != null ? spell.getTexture() : "");
            commandBuilder.set("#SpellPanel[%s] #Name.Text".formatted(i), spell.getName());
            commandBuilder.set("#SpellPanel[%s].TooltipText".formatted(i),
                    spell.getDescription() != null ? spell.getDescription() : "");
            commandBuilder.set("#SpellPanel[%s] #Level.Text".formatted(i), String.valueOf(spell.getLevel()));
            eventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#SpellPanel[%s]".formatted(i),
                    new EventData()
                            .append("Action", Infuse.Action.UpdateSlot)
                            .append("ItemId", spell.getItemId() != null ? spell.getItemId() : "Scroll_Fireball"));
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

    private void openSlot(Infuse data) {
        LOGGER.atInfo().log("Opening slot: " + data.getSlot());

        var commandBuilder = new UICommandBuilder();

        // Reset the style of all slots to the default style before opening a new one
        for (int i = 0; i < SLOT_COUNT; i++) {
            commandBuilder.set("#WheelPanel[%s].Style".formatted(i), DEFAULT_STYLE);
        }

        // If the slot being opened is already the selected slot, close it and return
        if (data.getSlot() == selectedSlotIndex) {
            commandBuilder.set("#SpellPanel.Visible", false);
            sendUpdate(commandBuilder);
            return;
        }

        // Update the style of the selected slot to indicate it is open
        selectedSlotIndex = data.getSlot();
        commandBuilder.set("#WheelPanel[%s].Style".formatted(selectedSlotIndex), SELECTED_STYLE);
        commandBuilder.set("#SpellPanel.Visible", true);
        sendUpdate(commandBuilder);
    }

    private void updateSlot(Infuse data) {
        LOGGER.atInfo().log("Updating slot: " + selectedSlotIndex + " with item: " + data.getItemId());

        // Update the item and reset the style of the slot to the default style
        var commandBuilder = new UICommandBuilder();
        commandBuilder.set("#WheelPanel[%s] #Item.ItemId".formatted(selectedSlotIndex), data.getItemId());
        commandBuilder.set("#WheelPanel[%s].Style".formatted(selectedSlotIndex), DEFAULT_STYLE);
        commandBuilder.set("#SpellPanel.Visible", false);
        sendUpdate(commandBuilder);

        selectedSlotIndex = -1;
    }

    private void infuse() {
        close();
    }

}
