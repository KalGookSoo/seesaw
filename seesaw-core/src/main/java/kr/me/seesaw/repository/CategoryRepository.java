package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;

public interface CategoryRepository extends Repository<Category, String> {

    Category save(Category category);

    Category getReferenceById(String id);

    Optional<Category> findById(String id);

    void deleteById(String id);

    Collection<Category> findAllBySiteId(String siteId, Sort sort);

}
