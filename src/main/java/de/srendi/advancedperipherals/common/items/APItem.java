package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;
import java.util.function.Supplier;

public class APItem extends BaseItem {

    String turtleID;
    String pocketID;
    ITextComponent description;
    Supplier<Boolean> enabledSup;

    public APItem(Properties properties, String turtleID, String pocketID, ITextComponent description, Supplier<Boolean> enabledSup) {
        super(properties);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
        this.enabledSup = enabledSup;
    }

    public APItem(String turtleID, String pocketID, ITextComponent description, Supplier<Boolean> enabledSup) {
        super();
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
        this.enabledSup = enabledSup;
    }

    @Override
    public Optional<String> getTurtleID() {
        return turtleID == null ? Optional.empty() : Optional.of(turtleID);
    }

    @Override
    public Optional<String> getPocketID() {
        return pocketID == null ? Optional.empty() : Optional.of(pocketID);
    }

    @Override
    public ITextComponent getDescription() {
        return description;
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }
}
