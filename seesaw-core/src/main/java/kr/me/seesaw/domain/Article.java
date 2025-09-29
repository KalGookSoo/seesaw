package kr.me.seesaw.domain;

import jakarta.persistence.*;
import kr.me.seesaw.command.CreateArticleCommand;
import kr.me.seesaw.command.UpdateArticleCommand;
import kr.me.seesaw.domain.vo.ArticleType;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(exclude = {"category", "replies", "views"}, callSuper = true)
@ToString(exclude = {"category", "replies", "views"})

@Entity
@Table(name = "tb_article", indexes = {
        @Index(columnList = "category_id"),
        @Index(columnList = "parent_id")
})
@Comment("게시글")
@DynamicInsert
@DynamicUpdate
public class Article extends AbstractHierarchical<Article> {

    @Comment("노출여부")
    private boolean exposed;

    @Comment("고정여부")
    private boolean fixed;

    @Comment("고정순서")
    private Integer fixedOrder;

    @Comment("제목")
    private String title;

    @Comment("본문")
    @Column(columnDefinition = "TEXT")
    @Setter(AccessLevel.PUBLIC)
    private String content;

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private ArticleType type;

    @Comment("카테고리 식별자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<View> views = new ArrayList<>();

    public static Article create(CreateArticleCommand command) {
        Article article = new Article();
        Category category = new Category();
        category.setId(command.getCategoryId());
        article.category = category;
        article.type = command.getType();
        article.fixed = command.isFixed();
        article.fixedOrder = command.getFixedOrder();
        article.title = command.getTitle();
        article.content = command.getContent();
        return article;
    }

    public void update(UpdateArticleCommand command) {
        getCategory().setId(command.getCategoryId());
        this.type = command.getType();
        this.fixed = command.isFixed();
        this.fixedOrder = command.getFixedOrder();
        this.title = command.getTitle();
        this.content = command.getContent();
    }

}
