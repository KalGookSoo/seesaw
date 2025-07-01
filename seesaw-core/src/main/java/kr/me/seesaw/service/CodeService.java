package kr.me.seesaw.service;

import kr.me.seesaw.domain.Code;

import java.util.List;

/**
 * 코드 서비스
 */
public interface CodeService {

    List<Code> get();

    List<Code> findByName(String name);

}
