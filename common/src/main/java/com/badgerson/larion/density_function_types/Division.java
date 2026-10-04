// Original credit: https://github.com/klinbee/More-Density-Functions (thanks)
package com.badgerson.larion.density_function_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record Division(DensityFunction argument1, DensityFunction argument2) implements DensityFunction {
    public static final MapCodec<Division> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("argument1").forGetter(Division::argument1),
        DensityFunction.CODEC.fieldOf("argument2").forGetter(Division::argument2)).apply(instance, Division::new));

    @Override public DensitySampler compileSampler(CompileContext context) {
        DensitySampler left = argument1.compileSampler(context);
        DensitySampler right = argument2.compileSampler(context);
        return (PointSampler) (samplerContext, x, y, z) -> {
            float divisor = right.sampleValue(samplerContext, x, y, z);
            return divisor == 0 ? 0 : left.sampleValue(samplerContext, x, y, z) / divisor;
        };
    }
    @Override public DensityFunction rewriteChildren(DfRewriteRule rule) {
        return new Division(rule.rewrite(argument1), rule.rewrite(argument2));
    }
    @Override public Interval range() {
        Interval divisor = argument2.range();
        if (divisor.min() == 0 && divisor.max() == 0) return Interval.ofExact(0);
        if (divisor.contains(0)) return Interval.INFINITE;
        return Interval.div(argument1.range(), divisor);
    }
    @Override public int domainAxes() { return argument1.domainAxes() | argument2.domainAxes(); }
    @Override public MapCodec<Division> codec() { return CODEC; }
}
