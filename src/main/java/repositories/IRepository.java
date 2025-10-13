package repositories;

import model.IModel;

import java.util.Set;

public interface IRepository<T extends IModel> {
    void add(T t);
    Set<T> getAll();
    T get();
    void update(T t);
    void remove(T t);
}
