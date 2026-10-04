// Original credit: https://github.com/klinbee/More-Density-Functions (thanks)
package com.badgerson.larion.density_function_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record XCoord() implements DensityFunction {
    public static final MapCodec<XCoord> CODEC = MapCodec.unit(new XCoord());
    @Override public DensitySampler compileSampler(CompileContext context) {
        return (PointSampler) (samplerContext, x, y, z) -> Math.clamp(x, -30_000_000, 30_000_000);
    }
    @Override public DensityFunction rewriteChildren(DfRewriteRule rule) { return this; }
    @Override public Interval range() { return Interval.of(-30_000_000, 30_000_000); }
    @Override public int domainAxes() { return AXIS_X; }
    @Override public MapCodec<XCoord> codec() { return CODEC; }
}
