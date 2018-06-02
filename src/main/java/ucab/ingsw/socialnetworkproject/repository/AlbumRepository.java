package ucab.ingsw.socialnetworkproject.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ucab.ingsw.socialnetworkproject.model.Album;

import java.util.List;

@Repository ("albumRepository")
public interface AlbumRepository extends CrudRepository<Album, Long>{
    List<Album> findByNameIgnoreCaseContaining(String name);
}
