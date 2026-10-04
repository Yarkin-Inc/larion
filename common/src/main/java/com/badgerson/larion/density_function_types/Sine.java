// Original credit: https://github.com/klinbee/More-Density-Functions (thanks)
package com.badgerson.larion.density_function_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record Sine(DensityFunction df) implements DensityFunction {
    public static final MapCodec<Sine> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(DensityFunction.CODEC.fieldOf("argument").forGetter(Sine::df)).apply(instance, Sine::new));

    @Override
    public DensitySampler compileSampler(CompileContext context) {
        DensitySampler input = df.compileSampler(context);
        return (PointSampler) (samplerContext, x, y, z) -> {
            float value = input.sampleValue(samplerContext, x, y, z);
            return (float) Math.sin(value);
        };
    }

    @Override public DensityFunction rewriteChildren(DfRewriteRule rule) { return new Sine(rule.rewrite(df)); }
    @Override public Interval range() { return Interval.of(-1, 1); }
    @Override public int domainAxes() { return df.domainAxes(); }
    @Override public MapCodec<Sine> codec() { return CODEC; }
}
