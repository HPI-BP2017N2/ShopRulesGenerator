package de.hpi.shoprulesgenerator.dto;

import java.io.Serializable;
public class Property<T> implements Serializable {
    private static final long serialVersionUID = -475706876964362699L;
    @SuppressWarnings("squid:S1948")
    private T value;


    public Property() {
    }
    public Property(T value) {
        this.value = value;
    }
    public T getValue() {
        return this.value;
    }
    public void setValue(T value) {
        this.value = value;
    }
    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
        return result;
    }
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Property<?> other = (Property)obj;
            if (this.value == null) {
                return other.value == null;
            } else return this.value.equals(other.value);
        }
    }
    public String toString() {
        return "Property [value=" + this.value + ']';
    }
}