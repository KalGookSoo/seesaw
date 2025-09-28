package kr.me.seesaw.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.me.seesaw.domain.View;
import lombok.*;
import org.hibernate.annotations.Comment;

@Schema(name = "ViewModel", description = "뷰 모델")
@ToString(exclude = {"article"})
@EqualsAndHashCode(exclude = {"article"}, callSuper = true)
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ViewModel extends BaseModel {

    @Comment("게시글 식별자")
    @Schema(description = "게시글 식별자(UUID)")
    private String articleId;

    @JsonBackReference
    private ArticleModel article;

    public ViewModel(View view) {
        setBaseModel(view);
        this.articleId = view.getArticleId();
    }
}
