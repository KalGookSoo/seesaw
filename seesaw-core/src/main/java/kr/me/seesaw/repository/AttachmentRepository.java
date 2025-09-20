package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Attachment;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends Repository<Attachment, String> {

    void save(Attachment attachment);

    Optional<Attachment> findById(String id);

    List<Attachment> findAllByReferenceIdIn(List<String> referenceIds);

    List<Attachment> findAllByIdIn(Collection<String> ids);

    void deleteAllInBatch(Iterable<Attachment> entities);

}
