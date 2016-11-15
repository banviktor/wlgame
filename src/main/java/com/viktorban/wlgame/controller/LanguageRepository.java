package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.Language;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * CRUD repository for Languages.
 *
 * @see com.viktorban.wlgame.model.Language
 */
@PreAuthorize("denyAll()")
public interface LanguageRepository extends CrudRepository<Language, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("permitAll()")
    Language findOne(String s);

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("permitAll()")
    Iterable<Language> findAll();

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("permitAll()")
    Iterable<Language> findAll(Iterable<String> iterable);

}
