package com.badgerson.larion.density_function_types;

import net.minecraft.world.level.levelgen.densityfunction.*;

/** Point evaluation fallback for Larion's custom density operations. */
@FunctionalInterface
interface PointSampler extends DensitySampler {
    @Override
    default void sampleVolume(SamplerContext context, DensityBuffer buffer, DensityVolume volume) {
        DensitySampler.sampleVolumeNaive(context, buffer, volume, this);
    }
}
