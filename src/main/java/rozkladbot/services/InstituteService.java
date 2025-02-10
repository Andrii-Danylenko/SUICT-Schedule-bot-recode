package rozkladbot.services;

import rozkladbot.entities.Institute;

public interface InstituteService extends BasicCrudService<Institute> {
    Institute getByName(String name);
}
