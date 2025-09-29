package kr.me.seesaw.domain;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import kr.me.seesaw.command.CreateCategoryCommand;
import kr.me.seesaw.command.UpdateCategoryCommand;
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
@EqualsAndHashCode(exclude = {"site", "articles"}, callSuper = true)
@ToString(exclude = {"site", "articles"})

@Entity
@Table(name = "tb_category", indexes = {
        @Index(columnList = "site_id"),
        @Index(columnList = "parent_id")
})
@DynamicInsert
@DynamicUpdate
@Comment("카테고리")
public class Category extends AbstractHierarchical<Category> {

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Article> articles = new ArrayList<>();

    public static Category create(CreateCategoryCommand command) {
        Category category = new Category();
        category.name = command.getName();
        category.description = command.getDescription();
        category.type = command.getType();
        category.siteExposed = command.isSiteExposed();
        category.siteExposedOrder = command.getSiteExposedOrder();
        category.exposed = command.isExposed();
        category.sequence = command.getSequence();

        Site site = new Site();
        site.setId(command.getSiteId());
        category.site = site;

        return category;
    }

    public void update(UpdateCategoryCommand command) {
        this.name = command.getName();
        this.description = command.getDescription();
        this.type = command.getType();
        this.siteExposed = command.isSiteExposed();
        this.siteExposedOrder = command.getSiteExposedOrder();
        this.exposed = command.isExposed();
        this.sequence = command.getSequence();

        this.getSite().setId(command.getSiteId());
    }

}
