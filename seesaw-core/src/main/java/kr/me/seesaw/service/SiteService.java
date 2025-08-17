package kr.me.seesaw.service;

import kr.me.seesaw.command.CreateSiteCommand;
import kr.me.seesaw.domain.Site;

import java.util.List;

public interface SiteService {

    Site getSiteById(String id);

    Site getSite(String domainName);

    Site getSiteContext(String domainName);

    List<Site> getOwnSites(String username);

    Site createSite(CreateSiteCommand command);

    Site updateSite(String id, CreateSiteCommand command);

    void deleteSite(String id);

}
