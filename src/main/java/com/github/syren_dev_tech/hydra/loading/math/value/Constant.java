package com.github.syren_dev_tech.hydra.loading.math.value;

import com.github.syren_dev_tech.hydra.loading.math.MathValue;

/**
 * {@link MathValue} value supplier
 *
 * <p>
 * <b>Contract:</b>
 * <br>
 * An immutable double value
 */
public record Constant(double value) implements MathValue {
    @Override
    public double get() {
        return this.value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
