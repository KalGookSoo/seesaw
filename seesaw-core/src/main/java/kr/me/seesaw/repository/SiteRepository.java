package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Site;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends Repository<Site, String> {

    Site getReferenceById(String id);

    Site save(Site site);

    Optional<Site> findById(String id);

    Optional<Site> findByDomainName(String domainName);

    List<Site> findAllByIdIn(List<String> ids);

}
