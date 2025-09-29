package kr.me.seesaw.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@ToString(exclude = {"parent", "children"})
@EqualsAndHashCode(exclude = {"parent", "children"}, callSuper = true)
@Setter(AccessLevel.PROTECTED)
@Getter
abstract public class AbstractHierarchical<T> extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @Comment("부모 식별자")
    protected T parent;

    @OneToMany(mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    protected List<T> children = new ArrayList<>();

}
