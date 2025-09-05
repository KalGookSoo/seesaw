package kr.me.seesaw.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter
@SuperBuilder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED, force = true)
public abstract class BaseModel {
    private final String id;

    private final String createdBy;

    private final String createdIp;

    private final LocalDateTime createdDate;

    private final String lastModifiedBy;

    private final String lastModifiedIp;

    private final LocalDateTime lastModifiedDate;
}
