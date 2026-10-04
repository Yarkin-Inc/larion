package com.badgerson.larion;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

public enum SomewhatSteepMaterialCondition implements MaterialCondition {
    INSTANCE;
    public static final MapCodec<SomewhatSteepMaterialCondition> CODEC = MapCodec.unit(INSTANCE);
    @Override public MapCodec<SomewhatSteepMaterialCondition> codec() { return CODEC; }
    @Override public ConditionEvaluator compile(MaterialRuleContext context) {
        return new SomewhatSteepSlopePredicate(context);
    }
}
