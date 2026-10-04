// Original credit: https://github.com/klinbee/More-Density-Functions (thanks)
package com.badgerson.larion.density_function_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record Signum(DensityFunction df) implements DensityFunction {
    public static final MapCodec<Signum> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(DensityFunction.CODEC.fieldOf("argument").forGetter(Signum::df)).apply(instance, Signum::new));

    @Override
    public DensitySampler compileSampler(CompileContext context) {
        DensitySampler input = df.compileSampler(context);
        return (PointSampler) (samplerContext, x, y, z) -> {
            float value = input.sampleValue(samplerContext, x, y, z);
            return Math.signum(value);
        };
    }

    @Override public DensityFunction rewriteChildren(DfRewriteRule rule) { return new Signum(rule.rewrite(df)); }
    @Override public Interval range() { return Interval.sign(df.range()); }
    @Override public int domainAxes() { return df.domainAxes(); }
    @Override public MapCodec<Signum> codec() { return CODEC; }
}
