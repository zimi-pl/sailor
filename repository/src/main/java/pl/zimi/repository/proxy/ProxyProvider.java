package pl.zimi.repository.proxy;

import pl.zimi.repository.query.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyProvider {

    public static <T, U> T provide(Class<T> interfaceToProxy, Repository<U> portedContract) {
        InvocationHandler handler = new SimpleInvocationHandler<>(portedContract);
        return (T) Proxy.newProxyInstance(interfaceToProxy.getClassLoader(),
                new Class[] { interfaceToProxy },
                handler);
    }
}
