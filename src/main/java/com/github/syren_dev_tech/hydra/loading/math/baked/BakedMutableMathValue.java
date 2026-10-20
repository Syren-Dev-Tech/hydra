package com.github.syren_dev_tech.hydra.loading.math.baked;

import com.github.syren_dev_tech.hydra.animation.state.ControllerState;
import com.github.syren_dev_tech.hydra.loading.math.MathValue;
import com.github.syren_dev_tech.hydra.loading.math.value.Variable;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/// Mutable half of the [BakedMathValue] bimorphic pair
public record BakedMutableMathValue(MathValue value) implements BakedMathValue {
    @Override
    public double get(@Nullable ControllerState controllerState) {
        return this.value.get(controllerState);
    }

    @Override
    public Set<Variable> getUsedVariables() {
        return this.value.getUsedVariables();
    }
}
