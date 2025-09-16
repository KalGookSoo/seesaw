package kr.me.seesaw.event.listener;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import kr.me.seesaw.annotation.CreatedIp;
import kr.me.seesaw.annotation.LastModifiedIp;
import kr.me.seesaw.core.authentication.IpAddressExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * 엔티티의 IP 주소 감사를 처리하는 리스너 클래스
 */
public class IpAddressAuditingEntityListener {

    private static final Logger logger = LoggerFactory.getLogger(IpAddressAuditingEntityListener.class);

    /**
     * 엔티티 생성 시 IP 주소를 설정합니다.
     *
     * @param target 대상 엔티티 객체
     */
    @PrePersist
    public void setCreatedIp(Object target) {
        setIpField(target, CreatedIp.class, IpAddressExtractor.getCurrentIp());
    }

    /**
     * 엔티티 수정 시 IP 주소를 설정합니다.
     *
     * @param target 대상 엔티티 객체
     */
    @PreUpdate
    public void setLastModifiedIp(Object target) {
        setIpField(target, LastModifiedIp.class, IpAddressExtractor.getCurrentIp());
    }

    /**
     * 대상 엔티티의 IP 필드에 값을 설정합니다.
     *
     * @param target          대상 엔티티 객체
     * @param annotationClass 어노테이션 클래스
     * @param value           설정할 IP 값
     */
    private void setIpField(Object target, Class<? extends Annotation> annotationClass, String value) {
        Class<?> clazz = target.getClass();
        while (clazz != null) {
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(annotationClass))
                    .peek(field -> field.setAccessible(true))
                    .forEach(field -> {
                        try {
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
                            logger.error("IP 주소 설정 중 접근 오류 발생: {}", e.getMessage());
                            throw new RuntimeException("IP 주소 할당 중 오류가 발생했습니다");
                        }
                    });
            clazz = clazz.getSuperclass();
        }
    }

}
