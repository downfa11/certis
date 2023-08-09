package Certis.Web.repository;

import Certis.Web.entity.BoardTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardTable,Integer> {

    Page<BoardTable> findByTitleContaining(String searchKeyword, Pageable pageable);

}
