package basicallyiamfox.ani.util;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;

public abstract class GenericClass<T> {
    private final TypeToken<T> typeToken = new TypeToken<>(getClass()) { };
    private final Type rawType = typeToken.getRawType();
    private final Type type = typeToken.getType();

    public Type getRawType() {
        return rawType;
    }

    public Type getType() {
        return type;
    }
}
