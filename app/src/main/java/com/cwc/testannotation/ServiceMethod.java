package com.cwc.testannotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cuiweicong 2018/6/19
 */

public class ServiceMethod {
    Builder builder;

    public ServiceMethod(Builder builder) {
        this.builder = builder;
    }


    public Object sendRequest(Object[] args){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i ++) {
            if (i < builder.parameterNameList.size()) {
                String name = (String) builder.parameterNameList.get(i);
                sb.append(name);
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }


    static final class Builder<T>{
        final Method method;
        final Annotation[][] parameterAnnotationsArray;
        final Annotation[] methodAnnotations;
        Type responseType;
        String requestPath;
        List<String> parameterNameList;


        public Builder(Method method) {
            this.method = method;
            parameterNameList = new ArrayList<>();
            parameterAnnotationsArray = method.getParameterAnnotations();
            methodAnnotations = method.getAnnotations();
        }

        public ServiceMethod build(){
            responseType = method.getReturnType();
            for (Annotation annotation: methodAnnotations) {
                parseMethodAnnotations(annotation);
            }
            parseParameters();
            return new ServiceMethod(this);
        }

        private void parseMethodAnnotations(Annotation annotation){
            if (annotation instanceof Get) {
                Get getAnnotation = (Get) annotation;
                requestPath = getAnnotation.value();
            }
        }

        private void parseParameters() {
            for (Annotation[] parametersAnnotations : parameterAnnotationsArray) {
                if (parametersAnnotations == null) {
                    throw new RuntimeException("没有找到注解！");
                }
                for (Annotation annotation : parametersAnnotations) {
                    if (annotation instanceof Parameter) {
                        Parameter parameter = (Parameter) annotation;
                        String name = parameter.value();
                        parameterNameList.add(name);
                    }
                }
            }
        }

        private Class<?> getRawType(Type type) {
            if (type == null) throw new NullPointerException("type == null");

            if (type instanceof Class<?>) {
                // Type is a normal class.
                return (Class<?>) type;
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;

                // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
                // suspects some pathological case related to nested classes exists.
                Type rawType = parameterizedType.getRawType();
                if (!(rawType instanceof Class)) throw new IllegalArgumentException();
                return (Class<?>) rawType;
            }
            if (type instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) type).getGenericComponentType();
                return Array.newInstance(getRawType(componentType), 0).getClass();
            }
            if (type instanceof TypeVariable) {
                // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
                // type that's more general than necessary is okay.
                return Object.class;
            }
            if (type instanceof WildcardType) {
                return getRawType(((WildcardType) type).getUpperBounds()[0]);
            }

            throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                    + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
        }
    }

}
