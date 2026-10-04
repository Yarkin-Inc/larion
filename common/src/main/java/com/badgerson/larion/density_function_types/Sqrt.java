package com.badgerson.larion.density_function_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record Sqrt(DensityFunction df) implements DensityFunction {
    public static final MapCodec<Sqrt> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(DensityFunction.CODEC.fieldOf("argument").forGetter(Sqrt::df)).apply(instance, Sqrt::new));

    @Override
    public DensitySampler compileSampler(CompileContext context) {
        DensitySampler input = df.compileSampler(context);
        return (PointSampler) (samplerContext, x, y, z) -> {
            float value = input.sampleValue(samplerContext, x, y, z);
            return value <= 0 ? 0 : (float) Math.sqrt(value);
        };
    }

    @Override public DensityFunction rewriteChildren(DfRewriteRule rule) { return new Sqrt(rule.rewrite(df)); }
    @Override public Interval range() { return Interval.of((float) Math.sqrt(Math.max(0, df.range().min())), (float) Math.sqrt(Math.max(0, df.range().max()))); }
    @Override public int domainAxes() { return df.domainAxes(); }
    @Override public MapCodec<Sqrt> codec() { return CODEC; }
}
