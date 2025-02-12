package rozkladbot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rozkladbot.entities.PairLink;

import java.util.List;

@Repository
public interface PairLinkRepo extends JpaRepository<PairLink, Long> {
    List<PairLink> findByName(String name);

    @Query(nativeQuery = true, value = "SELECT * FROM pair_links WHERE group_id = ?1 AND name = ?2 AND type = ?3")
    PairLink getByGroupIdAndLessonNameAndLessonType(long groupId, String lessonName, String lessonType);

    boolean existsByGroupIdAndNameAndLinkAndType(long groupId, String name, String link, String type);
}
