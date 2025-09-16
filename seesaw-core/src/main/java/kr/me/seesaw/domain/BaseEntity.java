package kr.me.seesaw.domain;

import jakarta.persistence.*;
import kr.me.seesaw.annotation.CreatedIp;
import kr.me.seesaw.annotation.LastModifiedIp;
import kr.me.seesaw.event.listener.IpAddressAuditingEntityListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, IpAddressAuditingEntityListener.class})
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
abstract public class BaseEntity implements Serializable {

    @Id
    @UuidGenerator
    @Column(length = 36, nullable = false, updatable = false)
    @Comment("식별자")
    private String id;

    @CreatedBy
    @Comment("생성자")
    private String createdBy;

    @CreatedIp
    @Column(length = 45, updatable = false)
    @Comment("생성 IP")
    private String createdIp;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("생성일시")
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Comment("수정자")
    private String lastModifiedBy;

    @LastModifiedIp
    @Column(length = 45, updatable = false)
    @Comment("수정 IP")
    private String lastModifiedIp;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("수정일시")
    private LocalDateTime lastModifiedDate;

    @Version
    @Comment("버전")
    private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
