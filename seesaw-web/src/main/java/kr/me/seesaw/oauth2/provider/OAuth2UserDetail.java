package kr.me.seesaw.oauth2.provider;

public interface OAuth2UserDetail {

    String getProviderId();

    String getProvider();

    String getEmail();

    String getName();

}
