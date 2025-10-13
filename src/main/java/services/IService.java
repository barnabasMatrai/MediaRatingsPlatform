package services;

import java.util.Set;

public interface IService<T> {
    void add(T t);
    Set<T> getAll();
    T get();
    void update(T t);
    void remove(T t);
}
