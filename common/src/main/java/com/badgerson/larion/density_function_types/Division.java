// Original credit: https://github.com/klinbee/More-Density-Functions (thanks)
package com.badgerson.larion.density_function_types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public record Division(DensityFunction argument1, DensityFunction argument2) implements DensityFunction {

    private static final MapCodec<Division> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
            .group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(Division::argument1),
                    DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(Division::argument2))
            .apply(instance, (Division::new)));
    public static final KeyDispatchDataCodec<Division> CODEC = DensityFunctions.makeCodec(MAP_CODEC);

    @Override
    public double compute(FunctionContext context) {
        double dividendValue = this.argument1.compute(context);
        double divisorValue = this.argument2.compute(context);

        if (divisorValue == 0) {
            return 0.0;
        }

        double result = dividendValue / divisorValue;

        return result;
    }

    @Override
    public void fillArray(double[] densities, ContextProvider provider) {
        provider.fillAllDirectly(densities, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(new Division(this.argument1.mapAll(visitor), this.argument2.mapAll(visitor)));
    }

    @Override
    public DensityFunction argument1() {
        return argument1;
    }

    @Override
    public DensityFunction argument2() {
        return argument2;
    }

    @Override
    public double minValue() {
        return bound(false);
    }

    @Override
    public double maxValue() {
        return bound(true);
    }

    private double bound(boolean upper) {
        double aMin = argument1.minValue();
        double aMax = argument1.maxValue();
        double bMin = argument2.minValue();
        double bMax = argument2.maxValue();
        double fallback = upper ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

        // compute() explicitly returns zero for a zero divisor.
        if (bMin == 0.0 && bMax == 0.0) {
            return 0.0;
        }
        // Intervals crossing zero may produce arbitrarily large quotients.
        // Non-finite input bounds cannot safely be handled by endpoint division.
        if (!Double.isFinite(aMin) || !Double.isFinite(aMax)
                || !Double.isFinite(bMin) || !Double.isFinite(bMax)
                || (bMin <= 0.0 && bMax >= 0.0)) {
            return fallback;
        }

        double q1 = aMin / bMin;
        double q2 = aMin / bMax;
        double q3 = aMax / bMin;
        double q4 = aMax / bMax;
        return upper
                ? Math.max(Math.max(q1, q2), Math.max(q3, q4))
                : Math.min(Math.min(q1, q2), Math.min(q3, q4));
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }
}
