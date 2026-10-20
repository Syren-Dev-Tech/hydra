package com.github.syren_dev_tech.hydra.loading.math.baked;

import com.github.syren_dev_tech.hydra.loading.math.MathValue;
import com.github.syren_dev_tech.hydra.loading.math.value.Constant;

/// Extension of [MathValue] to reduce its megamorphic call point to a bimorphic one, with only the mutable
/// values resulting in a megamorphic call
public sealed interface BakedMathValue extends MathValue permits BakedImmutableMathValue, BakedMutableMathValue {
    static BakedMathValue create(MathValue value) {
        if (value instanceof BakedMathValue bakedValue) {
            return bakedValue;
        }

        if (value.isMutable()) {
            return new BakedMutableMathValue(value);
        }

        return value instanceof Constant constant ?
               new BakedImmutableMathValue(constant) :
               new BakedImmutableMathValue(new Constant(value.get(null)));
    }
}
