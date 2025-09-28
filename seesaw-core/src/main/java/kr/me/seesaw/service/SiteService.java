package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateSiteCommand;
import kr.me.seesaw.domain.Site;
import kr.me.seesaw.model.SiteModel;

import java.io.IOException;
import java.util.List;

public interface SiteService {

    Site getSiteById(String id);

    SiteModel getSiteByDomainName(String domainName);

    SiteModel getSiteContext(String domainName);

    List<SiteModel> getOwnSites(String username);

    SiteModel createSite(CreateSiteCommand command) throws IOException;

    SiteModel updateSite(String id, CreateSiteCommand command) throws IOException;

    void deleteSite(String id);

}
