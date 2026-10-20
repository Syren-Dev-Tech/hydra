package com.github.syren_dev_tech.hydra.loading.math.function.generic;

import org.jspecify.annotations.Nullable;
import com.github.syren_dev_tech.hydra.animation.state.ControllerState;
import com.github.syren_dev_tech.hydra.loading.math.MathValue;
import com.github.syren_dev_tech.hydra.loading.math.function.MathFunction;

/// [MathFunction] value supplier
///
/// **Contract:**
///
/// Returns the square root of the input value
public final class SqrtFunction extends MathFunction {
    private final MathValue value;

    public SqrtFunction(MathValue... values) {
        super(values);

        this.value = values[0];
    }

    @Override
    public String getName() {
        return "math.sqrt";
    }

    @Override
    public double compute(@Nullable ControllerState controllerState) {
        return Math.sqrt(Math.max(0, this.value.get(controllerState)));
    }

    @Override
    public void validate(MathValue... inputs) throws IllegalArgumentException {
        super.validate(inputs);

        if (!this.value.isMutable() && this.value.get(null) < 0)
            throw new IllegalArgumentException(String.format("Sqrt function %s provided a negative value!", this));
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
