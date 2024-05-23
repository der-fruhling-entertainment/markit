package net.derfruhling.minecraft.markit;

import java.util.Objects;

public interface Keeper<T> {
    T get();
    void set(T value);

    final class Value<T> implements Keeper<T> {
        private T value;

        public Value(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void set(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Value) obj;
            return Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Value[value=" + value + ']';
        }
    }
}
