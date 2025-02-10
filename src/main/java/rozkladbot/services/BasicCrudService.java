package rozkladbot.services;

import java.util.Collection;
import java.util.List;

public interface BasicCrudService<T> {
    List<T> getAll();

    T getById(long id);

    T save(T value);

    void saveAll(Collection<T> value);
}
