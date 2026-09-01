package dev.pixelforge.specked.mixin;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    // Reserved for future key intercepts (e.g. custom sprint toggle key)
}
