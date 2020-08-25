package io.github.foundationgames.mealapi.util;

import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.util.Identifier;

public class MAUtil {

    public static final String MOD_ID = "mealapi";

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static double maximum(double value, double maximum) {
        if(value >= maximum) {
            return maximum;
        }
        return value;
    }

    public static double minimum(double value, double minimum) {
        if(value <= minimum) {
            return minimum;
        }
        return value;
    }

    public static double split(AtomicDouble value, double amount, double minimum) {
        double val = value.get();
        double nv = (int)minimum(val - amount, minimum);
        value.set(nv);
        return val - nv;
    }
    public static double split(AtomicDouble value, double amount) {
        return split(value, amount, 0);
    }
}
