package kr.me.seesaw;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 컴포넌트스캔 범위를 명시적으로 테스트 스코프에서 잡아줘야 테스트스코프의 빈 주입 패키지 범위를 IDE Tool에서 경고를 발생시키지 않는다. 이 클래스는 없어도 테스트가 작동은 한다.
 */
@SpringBootApplication
public class SeesawCoreApplicationTest {
}
