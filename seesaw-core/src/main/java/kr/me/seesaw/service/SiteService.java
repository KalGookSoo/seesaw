package kr.me.seesaw.service;

import kr.me.seesaw.domain.Site;

public interface SiteService {

    Site getSite(String domainName);

    Site getSiteContext(String domainName);

}
