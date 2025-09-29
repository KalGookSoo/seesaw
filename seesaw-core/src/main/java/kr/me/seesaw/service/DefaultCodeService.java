package kr.me.seesaw.service;

import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.domain.Code;
import kr.me.seesaw.model.CodeModel;
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
    public List<CodeModel> get() {
        List<CodeModel> models = codeRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"))
                .stream()
                .map(CodeModel::new)
                .toList();
        return HierarchicalFactory.build(models);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CodeModel> findByName(String name) {
        return codeRepository.findByName(name)
                .stream()
                .map(CodeModel::new)
                .toList();
    }

}
