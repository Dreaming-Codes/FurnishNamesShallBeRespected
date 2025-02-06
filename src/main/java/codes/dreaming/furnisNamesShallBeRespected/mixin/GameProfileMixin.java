package codes.dreaming.furnisNamesShallBeRespected.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin {
    @Shadow public abstract UUID getId();

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void fixSelfName(CallbackInfoReturnable<String> cir) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        //noinspection EqualsBetweenInconvertibleTypes
        if (!MinecraftClient.getInstance().player.getGameProfile().equals(this)) {
            return;
        }

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) {
            return;
        }

        PlayerListEntry entry = networkHandler.getPlayerListEntry(this.getId());
        if (entry == null) {
            return;
        }

        Text nickText = entry.getDisplayName();
        if (nickText == null) {
            return;
        }

        cir.setReturnValue(nickText.getString());
    }
}
