package com.badgerson.larion;

import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public class SomewhatSteepSlopePredicate extends MaterialRuleContext.LazyXZCondition {
    public SomewhatSteepSlopePredicate(MaterialRuleContext context) { super(context); }
    @Override protected boolean compute() {
        return Math.abs(context.surfaceGradientX()) > 1 || Math.abs(context.surfaceGradientZ()) > 1;
    }
}
