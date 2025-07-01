package kr.me.seesaw.search;

import org.springframework.web.util.UriComponentsBuilder;

public interface UriComponentsProvider {
    default UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsBuilder.newInstance();
    }
}
