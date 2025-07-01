package kr.me.seesaw.search;

import kr.me.seesaw.domain.vo.CategoryType;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Data
public class ArticleSearch implements UriComponentsProvider {

    private String id;

    private String categoryId;

    private CategoryType categoryType;

    private ViewType viewType = ViewType.TABLE;

    private String keyField;

    private String keyWord;

    @Override
    public UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsProvider.super.getUriComponentsBuilder()
                .queryParamIfPresent("categoryId", Optional.ofNullable(categoryId))
                .queryParamIfPresent("categoryType", Optional.ofNullable(categoryType))
                .queryParamIfPresent("viewType", Optional.ofNullable(viewType))
                .queryParamIfPresent("keyField", Optional.ofNullable(keyField))
                .queryParamIfPresent("keyWord", Optional.ofNullable(keyWord));
    }

    @Getter
    @RequiredArgsConstructor
    public enum ViewType {
        TABLE("목록"),
        CARD("카드");
        private final String description;
    }
}
