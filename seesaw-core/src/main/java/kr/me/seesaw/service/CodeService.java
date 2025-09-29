package kr.me.seesaw.service;

import kr.me.seesaw.model.CodeModel;

import java.util.List;

/**
 * 코드 서비스
 */
public interface CodeService {

    List<CodeModel> get();

    List<CodeModel> findByName(String name);

}
