package rozkladbot.services;

import rozkladbot.entities.Institute;

import java.util.List;

public interface InstituteService extends BasicCrudService<Institute> {
    Institute getByName(String name);

    List<Institute> findAllEager();
}
