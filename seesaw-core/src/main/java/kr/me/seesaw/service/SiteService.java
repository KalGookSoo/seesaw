package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateSiteCommand;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.model.SiteModel;

import java.io.IOException;
import java.util.List;

public interface SiteService {

    Site getSiteById(String id);

    Site getSiteByDomainName(String domainName);

    Site getSiteContext(String domainName);

    List<Site> getOwnSites(String username);

    SiteModel createSite(CreateSiteCommand command) throws IOException;

    Site updateSite(String id, CreateSiteCommand command) throws IOException;

    void deleteSite(String id);

}
