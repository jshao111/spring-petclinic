/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.util.Collection;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for <code>Specialty</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data See here: http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 *
 * @author Jackie Shao
 */
public interface SpecialtyRepository extends Repository<Specialty, Integer> {

    /**
     * Retrieve all <code>Specialty</code>s from the data store.
     *
     * @return a <code>Collection</code> of <code>Specialty</code>s
     */
    @Query("SELECT specialty FROM Specialty specialty ORDER BY specialty.name")
    @Transactional(readOnly = true)
    Collection<Specialty> findAll() throws DataAccessException;

    /**
     * Retrieve an {@link Specialty} from the data store by id.
     * @param id the id to search for
     * @return the {@link Specialty} if found
     */
    @Query("SELECT specialty FROM Specialty specialty WHERE specialty.id =:id")
    @Transactional(readOnly = true)
    Specialty findById(@Param("id") Integer id);

    /**
     * Retrieve an {@link Specialty} from the data store by name.
     * @param name the id to search for
     * @return the {@link Specialty} if found
     */
    @Query("SELECT specialty FROM Specialty specialty WHERE specialty.name =:name")
    @Transactional(readOnly = true)
    Specialty findByName(@Param("name") String name);

    /**
     * Save an {@link Vet} to the data store, either inserting or updating it.
     * @param specialty the {@link Vet} to save
     */
    void save(Specialty specialty);

}
