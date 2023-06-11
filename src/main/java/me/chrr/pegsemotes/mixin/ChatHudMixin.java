package me.chrr.pegsemotes.mixin;

import me.chrr.pegsemotes.render.EmoteRenderHelper;
import me.chrr.pegsemotes.render.PositionedEmote;
import me.chrr.pegsemotes.text.TextReaderVisitor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I"))
    private int drawWithShadow(TextRenderer textRenderer, MatrixStack matrices, OrderedText text, float x, float y, int color) {
        TextReaderVisitor textReaderVisitor = new TextReaderVisitor();
        text.accept(textReaderVisitor);

        float emoteAlpha = (float) (color >> 24 & 255) / 255.0f;

        List<PositionedEmote> positionedEmoteList = EmoteRenderHelper.extractEmotes(textReaderVisitor, textRenderer, x, y);

        matrices.translate(0, -0.5f, 0);
        for (PositionedEmote positionedEmote : positionedEmoteList) {
            EmoteRenderHelper.drawEmote(matrices, positionedEmote, emoteAlpha);
        }
        matrices.translate(0, 0.5, 0);

        return textRenderer.draw(matrices, textReaderVisitor.getOrderedText(), x, y, color);
    }
}
