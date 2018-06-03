package ucab.ingsw.socialnetworkproject.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ucab.ingsw.socialnetworkproject.model.Media;

@Repository("mediaRepository")
public interface MediaRepository extends CrudRepository<Media, Long> {
}
