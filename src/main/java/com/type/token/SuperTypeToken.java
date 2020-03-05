package com.type.token;

import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class SuperTypeToken {
    static class TypeSafeMap {
        Map<Type, Object> map = new HashMap<>();

        <T> void put(TypeReference<T> tr, T value) {
            map.put(tr.type, value);
        }

        <T> T get(TypeReference<T> tr) {
            if(tr.type instanceof Class<?>) {
                return ((Class<T>)tr.type).cast(map.get(tr.type));
            } else {
                return ((Class<T>)((ParameterizedType) tr.type).getRawType()).cast(map.get(tr.type));
            }
        }
    }

    static class TypeReference<T> {
        Type type;

        public TypeReference() {
            Type stype = getClass().getGenericSuperclass();
            if(stype instanceof ParameterizedType) {
                this.type = ((ParameterizedType)stype).getActualTypeArguments()[0];
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass().getSuperclass() != o.getClass().getSuperclass()) return false;
            TypeReference<?> that = (TypeReference<?>) o;
            return type.equals(that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }

    public static void main(String[] args) {
        TypeReference<List<String>> t = new TypeReference<List<String>>() {};
        System.out.println(t.type);

        TypeSafeMap m = new TypeSafeMap();
        m.put(new TypeReference<Integer>(){}, 1);
        m.put(new TypeReference<String>(){}, "String");
        m.put(new TypeReference<List<Integer>>(){}, Arrays.asList(1, 2, 3));
        m.put(new TypeReference<List<String>>(){}, Arrays.asList("a", "b", "c"));
        m.put(new TypeReference<List<List<String>>>(){}, Arrays.asList(Arrays.asList("a"), Arrays.asList("b"), Arrays.asList("c")));

        System.out.println(m.get(new TypeReference<Integer>(){}));
        System.out.println(m.get(new TypeReference<String>(){}));
        System.out.println(m.get(new TypeReference<List<Integer>>(){}));
        System.out.println(m.get(new TypeReference<List<String>>(){}));
        System.out.println(m.get(new TypeReference<List<List<String>>>(){}));

        ResolvableType rt = ResolvableType.forInstance(new TypeReference<List<String>>(){});
        System.out.println(rt.getSuperType().getGeneric(0).getType());
        System.out.println(rt.getSuperType().getGeneric(0).getNested(2).getType());
    }
}
