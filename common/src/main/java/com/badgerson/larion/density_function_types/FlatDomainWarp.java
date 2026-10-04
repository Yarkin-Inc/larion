package com.badgerson.larion.density_function_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.*;

public record FlatDomainWarp(DensityFunction input, DensityFunction warpX, DensityFunction warpZ) implements DensityFunction {
    public static final MapCodec<FlatDomainWarp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DensityFunction.CODEC.fieldOf("input").forGetter(FlatDomainWarp::input),
        DensityFunction.CODEC.fieldOf("warp_x").forGetter(FlatDomainWarp::warpX),
        DensityFunction.CODEC.fieldOf("warp_z").forGetter(FlatDomainWarp::warpZ)).apply(instance, FlatDomainWarp::new));

    @Override public DensitySampler compileSampler(CompileContext context) {
        DensitySampler source = input.compileSampler(context);
        DensitySampler xWarp = warpX.compileSampler(context);
        DensitySampler zWarp = warpZ.compileSampler(context);
        return (PointSampler) (samplerContext, x, y, z) -> source.sampleValue(samplerContext,
            x + (int) xWarp.sampleValue(samplerContext, x, y, z), y,
            z + (int) zWarp.sampleValue(samplerContext, x, y, z));
    }
    @Override public DensityFunction rewriteChildren(DfRewriteRule rule) {
        return new FlatDomainWarp(rule.rewrite(input), rule.rewrite(warpX), rule.rewrite(warpZ));
    }
    @Override public Interval range() { return input.range(); }
    @Override public int domainAxes() { return input.domainAxes() | warpX.domainAxes() | warpZ.domainAxes(); }
    @Override public MapCodec<FlatDomainWarp> codec() { return CODEC; }
}
