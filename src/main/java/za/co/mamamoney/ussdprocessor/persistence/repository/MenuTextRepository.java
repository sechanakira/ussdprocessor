package za.co.mamamoney.ussdprocessor.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.mamamoney.ussdprocessor.persistence.model.MenuText;

@Repository
public interface MenuTextRepository extends JpaRepository<MenuText, Long> {

    @Query(value = "SELECT mt FROM MenuText mt WHERE mt.menuNumber = :menuNumber AND mt.language = :language")
    MenuText findByMenuNumberAndLanguage(@Param("menuNumber") final Integer menuNumber,
                                         @Param("language") final String language);

    MenuText findMenuTextByMenuNumber(final Integer menuNumber);
}
