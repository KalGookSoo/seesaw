package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
import kr.me.seesaw.core.hierarchy.Hierarchical;
import kr.me.seesaw.domain.vo.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_category")
@DynamicInsert
@DynamicUpdate
@Comment("카테고리")
public class Category extends AbstractHierarchical<Category> implements Hierarchical<Category, String> {

    @Comment("이름")
    private String name;

    @Comment("설명")
    private String description;

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private CategoryType type;

    @Comment("사이트 노출여부")
    private boolean siteExposed;
    
    @Comment("사이트 노출순서")
    private int siteExposedOrder;

    @Comment("노출여부")
    private boolean exposed;

    @Comment("순서")
    private Integer sequence;

    @Comment("사이트 식별자")
    @Column(length = 36)
    private String siteId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Site site;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Article> articles = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Article> recentArticles = new ArrayList<>();

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Notification> notifications = new ArrayList<>();

    @Override
    public void addChild(Category child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public static Category create(CreateCategoryCommand command) {
        throw new UnsupportedOperationException();
    }

    public void update(UpdateCategoryCommand command) {
        throw new UnsupportedOperationException();
    }

    public void joinArticles(List<Article> articles) {
        articles.stream().filter(this::isArticleForCategory).forEach(this::addArticle);
    }

    private boolean isArticleForCategory(Article article) {
        return getId().equals(article.getCategoryId());
    }

    public void addArticle(Article article) {
        articles.add(article);
        article.setCategoryId(getId());
    }

    public void addRecentArticle(Article article) {
        recentArticles.add(article);
    }
}
