package kr.me.seesaw.service;

import kr.me.seesaw.domain.Site;

import java.util.List;

public interface SiteService {

    Site getSite(String domainName);

    Site getSiteContext(String domainName);

    List<Site> getOwnSites(String username);
}
