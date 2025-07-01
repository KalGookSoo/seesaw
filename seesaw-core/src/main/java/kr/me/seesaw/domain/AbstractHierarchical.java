package kr.me.seesaw.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Setter(AccessLevel.PROTECTED)
@Getter
abstract public class AbstractHierarchical<T> extends BaseEntity {

    @Comment("부모 식별자")
    @Column(length = 36)
    protected String parentId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    protected T parent;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    protected List<T> children = new ArrayList<>();

}
