package kr.me.seesaw.repository;

import kr.me.seesaw.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
