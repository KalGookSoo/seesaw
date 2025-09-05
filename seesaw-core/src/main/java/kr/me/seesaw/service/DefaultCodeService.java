package kr.me.seesaw.service;

import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.domain.Code;
import kr.me.seesaw.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class DefaultCodeService implements CodeService {
    private final CodeRepository codeRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Code> get() {
        List<Code> codes = codeRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"));
        return HierarchicalFactory.build(codes);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Code> findByName(String name) {
        return codeRepository.findByName(name);
    }
}
