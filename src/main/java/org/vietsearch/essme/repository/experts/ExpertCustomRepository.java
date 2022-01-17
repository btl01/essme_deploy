package org.vietsearch.essme.repository.experts;

import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.Query;
import org.vietsearch.essme.model.expert.Expert;

import java.util.List;

public interface ExpertCustomRepository {
    List<Object> getNumberOfExpertsInEachField();

//    @Query("{ $and : [ {$text:{$search : ?0}}, {'location.features.geometry': {$geoWithin: { $centerSphere: [ [ ?1, ?2 ], ?3/6378 ]}}}  ]}")
    Page<Expert> searchByLocationAndText(String what, String where, double radius);
}