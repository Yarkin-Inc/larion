package com.badgerson.larion.density_function_types;

import net.minecraft.world.level.levelgen.densityfunction.*;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DensityFunctionTest {
    @org.junit.jupiter.api.BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    private static float sample(DensityFunction function, int x, int z) {
        return function.compileSampler(null).sampleValue(SamplerContext.EMPTY_UNCACHED, x, 20, z);
    }

    @Test void divisionPreservesZeroDivisorAndBounds() {
        var zero = new Division(new ConstantFunction(12), new ConstantFunction(0));
        assertEquals(0, sample(zero, 0, 0));
        assertEquals(0, zero.range().min());
        assertEquals(0, zero.range().max());
        var signed = new Division(new XCoord(), new ConstantFunction(-2));
        assertEquals(-3, sample(signed, 6, 0));
        assertTrue(signed.range().contains(-3));
        var crossing = new Division(new ConstantFunction(1), new XCoord());
        assertEquals(Float.NEGATIVE_INFINITY, crossing.range().min());
        assertEquals(Float.POSITIVE_INFINITY, crossing.range().max());
    }

    @Test void unaryFunctionsKeepLarionSemantics() {
        assertEquals(0, sample(new Sqrt(new ConstantFunction(-4)), 0, 0));
        assertEquals(3, sample(new Sqrt(new ConstantFunction(9)), 0, 0));
        assertEquals(-1, sample(new Signum(new XCoord()), -4, 0));
        assertEquals(0, sample(new Signum(new XCoord()), 0, 0));
        assertEquals(1, sample(new Sine(new ConstantFunction((float) (Math.PI / 2))), 0, 0), 0.00001);
        assertEquals(30_000_000, sample(new XCoord(), 31_000_000, 0));
        assertEquals(-30_000_000, sample(new ZCoord(), 0, -31_000_000));
    }

    @Test void warpTruncatesOffsetsAndRewritesEveryChild() {
        var warp = new FlatDomainWarp(new XCoord(), new ConstantFunction(-1.9f), new ConstantFunction(2.9f));
        assertEquals(9, sample(warp, 10, 20));
        var zWarp = new FlatDomainWarp(new ZCoord(), new ConstantFunction(-1.9f), new ConstantFunction(2.9f));
        assertEquals(22, sample(zWarp, 10, 20));
        var rewritten = (FlatDomainWarp) warp.rewriteChildren(child -> new ConstantFunction(7));
        assertEquals(new ConstantFunction(7), rewritten.input());
        assertEquals(new ConstantFunction(7), rewritten.warpX());
        assertEquals(new ConstantFunction(7), rewritten.warpZ());
        assertEquals(DensityFunction.AXIS_X, warp.domainAxes());
        assertEquals(DensityFunction.AXIS_X | DensityFunction.AXIS_Z,
            new FlatDomainWarp(new XCoord(), new ZCoord(), new ConstantFunction(0)).domainAxes());
    }

    @Test void bulkSamplingMatchesPointSamplingIncludingZeroDivision() {
        DensityFunction[] functions = {
            new Division(new XCoord(), new ZCoord()),
            new Sqrt(new XCoord()), new Signum(new XCoord()), new Sine(new XCoord()),
            new FlatDomainWarp(new XCoord(), new ZCoord(), new ConstantFunction(0))
        };
        var volume = new DensityVolume(3, 2, 3, -1, 20, -1);
        for (var function : functions) {
            var sampler = function.compileSampler(null);
            var buffer = DensityBuffer.createUnpooled(volume.size());
            sampler.sampleVolume(SamplerContext.EMPTY_UNCACHED, buffer, volume);
            for (int x = -1; x <= 1; x++) for (int y = 20; y <= 21; y++) for (int z = -1; z <= 1; z++) {
                assertEquals(sampler.sampleValue(SamplerContext.EMPTY_UNCACHED, x, y, z),
                    buffer.get(volume.indexOfBlock(x, y, z)), function.toString());
            }
        }
    }
}
