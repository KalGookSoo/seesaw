package kr.me.seesaw.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

/**
 * 주소
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Address implements Serializable {

    private String zipcode;

    private String value;

    public static Address empty() {
        return new Address(null, null);
    }

    @Override
    public String toString() {
        return value + " [" + zipcode + "]";
    }

}
