package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Site;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface SiteRepository extends Repository<Site, String> {

    Site save(Site site);

    Optional<Site> findByDomainName(String domainName);

}
