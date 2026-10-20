package com.github.syren_dev_tech.hydra.loading.math.function.round;

import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animation.state.ControllerState;
import com.github.syren_dev_tech.hydra.loading.math.MathValue;
import com.github.syren_dev_tech.hydra.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the <a href="https://en.wikipedia.org/wiki/Hermite_polynomials">Hermite</a>> basis `3t^2 - 2t^3` curve interpolation value based on the input value
public final class HermiteBlendFunction extends MathFunction {
    private final MathValue valueA;

    public HermiteBlendFunction(MathValue... values) {
        super(values);

        this.valueA = values[0];
    }

    @Override
    public String getName() {
        return "math.hermite_blend";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        final double value = this.valueA.get(controllerState);

        return (3 * value * value) - (2 * value * value * value);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.valueA};
    }
}
