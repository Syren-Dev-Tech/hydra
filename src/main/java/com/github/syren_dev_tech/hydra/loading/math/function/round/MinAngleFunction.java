package com.github.syren_dev_tech.hydra.loading.math.function.round;

import com.github.syren_dev_tech.hydra.animation.state.ControllerState;
import com.github.syren_dev_tech.hydra.loading.math.MathValue;
import com.github.syren_dev_tech.hydra.loading.math.function.MathFunction;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Minimizes the magnitude of the input value angle (in degrees), clamped between -180 and 180 (inclusive)
public final class MinAngleFunction extends MathFunction {
    private final MathValue value;

    public MinAngleFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.min_angle";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Mth.wrapDegrees(this.value.get(controllerState));
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public MathValue[] getArgs() {
        return new MathValue[] {this.value};
    }
}
