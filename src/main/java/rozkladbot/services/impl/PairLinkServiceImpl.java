package rozkladbot.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rozkladbot.constants.ErrorConstants;
import rozkladbot.entities.PairLink;
import rozkladbot.exceptions.EntityAlreadyExistsException;
import rozkladbot.exceptions.NoSuchEntityFoundException;
import rozkladbot.repos.PairLinkRepo;
import rozkladbot.services.PairLinkService;

import java.util.Collection;
import java.util.List;

@Service("pairLinkServiceImpl")
public class PairLinkServiceImpl implements PairLinkService {
    private PairLinkRepo pairLinkRepo;

    public PairLinkServiceImpl(PairLinkRepo pairLinkRepo) {
        this.pairLinkRepo = pairLinkRepo;
    }

    @Override
    public List<PairLink> getAll() {
        return pairLinkRepo.findAll();
    }

    @Override
    public PairLink getById(long id) {
        return pairLinkRepo.findById(id).orElseThrow(() -> new NoSuchEntityFoundException(ErrorConstants.ENTITY_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<PairLink> getByName(String name) {
        return pairLinkRepo.findByName(name);
    }

    @Override
    public PairLink getByGroupIdAndLessonNameAndLessonType(long groupId, String lessonName, String lessonType) {
        return pairLinkRepo.getByGroupIdAndLessonNameAndLessonType(groupId, lessonName, lessonType);
    }

    @Override
    @Transactional
    public PairLink save(PairLink value) {
        if (pairLinkRepo.existsByGroupIdAndNameAndLinkAndType(
                value.getGroupId(),
                value.getName(),
                value.getLink(),
                value.getType())
        ) {
            throw new EntityAlreadyExistsException();
        }
        return pairLinkRepo.save(value);
    }

    @Override
    @Transactional
    public void saveAll(Collection<PairLink> value) {
        for (PairLink pairLink : value) {
            try {
                save(pairLink);
            } catch (EntityAlreadyExistsException ignored) {
            }
        }
    }
}
