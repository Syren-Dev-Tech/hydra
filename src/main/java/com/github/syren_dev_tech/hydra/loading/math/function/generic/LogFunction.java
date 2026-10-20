package com.github.syren_dev_tech.hydra.loading.math.function.generic;

import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animation.state.ControllerState;
import com.github.syren_dev_tech.hydra.loading.math.MathValue;
import com.github.syren_dev_tech.hydra.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the log value (Euler base) of the input value
public final class LogFunction extends MathFunction {
    private final MathValue value;

    public LogFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.ln";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.log((float)this.value.get(controllerState));
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
