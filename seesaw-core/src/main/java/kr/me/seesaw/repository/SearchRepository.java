package kr.me.seesaw.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchRepository<T, Q> {
    Page<T> search(Pageable pageable, Q q);
}
