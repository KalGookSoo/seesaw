package kr.me.seesaw.domain.vo;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

/**
 * 이메일
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Email implements Serializable {

    private String id;

    private String domain;

    public static Email empty() {
        return new Email(null, null);
    }

    @Override
    public String toString() {
        return Optional.ofNullable(id).orElse("") + "@" + Optional.ofNullable(domain).orElse("");
    }

}
