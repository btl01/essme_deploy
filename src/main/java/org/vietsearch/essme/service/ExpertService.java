package org.vietsearch.essme.service;

import org.vietsearch.essme.model.expert.Expert;

import java.util.List;

public interface ExpertService {
    List<Expert> getTop9ExpertDistinctByRA();
}
