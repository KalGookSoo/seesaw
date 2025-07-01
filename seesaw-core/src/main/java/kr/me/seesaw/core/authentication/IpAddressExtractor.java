package kr.me.seesaw.core.authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 공개 IP 주소를 추출하기 위한 인터페이스
 */
public interface IpAddressExtractor {

    /**
     * 현재 요청의 공개 IP 주소를 추출합니다.
     *
     * @return 추출된 IP 주소 문자열
     */
    static String getCurrentIp() {
        // HTTP 헤더에서 먼저 IP를 가져오기 시도 (공개 IP용)
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // X-Forwarded-For 헤더 먼저 확인 (프록시에서 일반적)
                String ip = request.getHeader("X-Forwarded-For");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    // X-Forwarded-For는 여러 IP를 포함할 수 있음 (클라이언트, 프록시1, 프록시2, ...), 첫 번째 IP 가져오기
                    int commaIndex = ip.indexOf(',');
                    if (commaIndex > 0) {
                        return ip.substring(0, commaIndex).trim();
                    }
                    return ip.trim();
                }

                // 공개 IP를 위한 다른 일반적인 헤더 확인
                String[] headers = {"X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR",
                        "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR",
                        "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};

                for (String header : headers) {
                    ip = request.getHeader(header);
                    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                        return ip.trim();
                    }
                }

                // 헤더를 찾지 못한 경우 요청에서 IP 가져오기
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            return "UNKNOWN";
        }
        return "UNKNOWN";
    }
}