package com.badgerson.larion.mixin;

import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.SteepCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SteepCondition.class)
public class SteepSlopePredicateMixin {
    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    private void larion$steep(MaterialRuleContext context, CallbackInfoReturnable<ConditionEvaluator> ci) {
        ci.setReturnValue(new MaterialRuleContext.LazyXZCondition(context) {
            @Override protected boolean compute() {
                return Math.abs(context.surfaceGradientX()) > 3 || Math.abs(context.surfaceGradientZ()) > 3;
            }
        });
    }
}
