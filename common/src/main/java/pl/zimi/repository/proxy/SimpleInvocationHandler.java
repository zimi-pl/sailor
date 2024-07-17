package pl.zimi.repository.proxy;

import pl.zimi.repository.query.Repository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SimpleInvocationHandler<T> implements InvocationHandler {

    private final Repository<T> repository;

    public SimpleInvocationHandler(Repository<T> repository) {
        this.repository = repository;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(repository, args);
    }

}
