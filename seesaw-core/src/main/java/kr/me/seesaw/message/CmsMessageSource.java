package kr.me.seesaw.message;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class CmsMessageSource extends ReloadableResourceBundleMessageSource {

    public String getMessage(String code) {
        return getMessage(code, null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, LocaleContextHolder.getLocale());
    }

}
