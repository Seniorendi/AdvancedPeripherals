package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic.OverpoweredWeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic.WeakMechanicSoulPeripheral;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class TurtleOverpoweredWeakMechanicSoul extends BaseTurtle {
    public TurtleOverpoweredWeakMechanicSoul() {
        super("overpowered_weak_mechanic_soul_turtle", "turtle.advancedperipherals.overpowered_weak_mechanic_soul", new ItemStack(Items.OVERPOWERED_WEAK_MECHANIC_SOUL.get()));
    }

    @Override
    protected WeakMechanicSoulPeripheral createPeripheral() {
        return new OverpoweredWeakMechanicSoulPeripheral("overpowered_weak_mechanic_soul", null);
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return null; //Null, the turtle uses the chunk controller item model. See BaseTurtle.java
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return null;
    }
}