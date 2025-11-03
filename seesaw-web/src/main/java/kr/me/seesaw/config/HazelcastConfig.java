package kr.me.seesaw.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.*;

@EnableCaching
@Configuration
@Profile({"prod"})
public class HazelcastConfig {

    private final String profiles;

    private final Map<String, List<String>> profilesMapper = new HashMap<>();

    // 클러스터링할 애플리케이션의 내부망 IP 엔트리를 넣어야함.
    // 현재는 동일한 서버에서 클러스터링하려고 127.0.0.1로 할당함
    private final List<String> productionMembers = List.of(
            "127.0.0.1:5700",
            "127.0.0.1:5701",
            "127.0.0.1:5702",
            "127.0.0.1:5703"
    );

    private void map() {
        profilesMapper.put("prod", productionMembers);
    }

    private List<String> getMembers() {
        return profilesMapper.getOrDefault(profiles, Collections.emptyList());
    }

    public HazelcastConfig(@Value("${spring.profiles.active:default}") String profiles) {
        this.profiles = profiles;
        map();
    }

    @Bean
    public Config hazelcast() {
        Config config = new Config();

        // 선택사항: 로깅 타입 설정
        config.setProperty("hazelcast.logging.type", "slf4j");

        // 선택사항: 클러스터 이름 설정
        config.setClusterName("session-clustering");

        // 선택사항: 인스턴스 이름 설정
        config.setInstanceName("session-clustering-instance");

        // 그룹 설정
        NetworkConfig networkConfig = config.getNetworkConfig();

        // 포트가 중복될 경우 자동 증가
        networkConfig.setPort(5700).setPortCount(100).setPortAutoIncrement(true);

        // TCP-IP 설정
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);

        TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
        tcpIpConfig.setEnabled(true);

        // 활성 프로필에 따라 멤버 설정
        tcpIpConfig.setMembers(getMembers());

        // 선택사항이지만 권장: 연결 타임아웃을 낮은 값으로 설정
        tcpIpConfig.setConnectionTimeoutSeconds(5);

        return config;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(@Qualifier("hazelcast") Config config) {
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }


}
