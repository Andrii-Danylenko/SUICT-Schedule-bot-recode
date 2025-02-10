package rozkladbot.services;

import rozkladbot.entities.PairLink;

import java.util.List;

public interface PairLinkService extends BasicCrudService<PairLink> {
    List<PairLink> getByName(String name);
    PairLink getByGroupIdAndLessonNameAndLessonType(long groupId, String lessonName, String lessonType);
}
