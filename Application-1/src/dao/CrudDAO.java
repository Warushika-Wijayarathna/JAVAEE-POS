package dao;

import java.util.List;

public interface CrudDAO<T> extends SuperDAO {
    boolean add(T entity) throws Exception;

    boolean delete(T entity) throws Exception;

    boolean update(T entity) throws Exception;

    Object search(T entity) throws Exception;

    List<T> getAll() throws Exception;

    T exist(String id) throws Exception;
}
