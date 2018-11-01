package com.cwc.testannotation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Cuiweicong 2018/6/19
 */

public class MyRetrofit {
    private final Map<Method, ServiceMethod> serviceMethodCache = new LinkedHashMap<>();

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <T> T create(final Class<T> service){
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] {service}, new
                InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                ServiceMethod serviceMethod = loadServiceMethod(method);
                return serviceMethod.sendRequest(args);
            }
        });
    }


    private ServiceMethod loadServiceMethod(Method method){
        ServiceMethod result;
        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder(method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }
}
