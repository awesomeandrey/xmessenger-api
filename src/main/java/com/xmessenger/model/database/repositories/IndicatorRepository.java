package com.xmessenger.model.database.repositories;

import com.xmessenger.model.database.entities.decorators.Indicator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndicatorRepository extends CrudRepository<Indicator, Integer> {
}