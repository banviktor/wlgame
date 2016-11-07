package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.Word;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * CRUD repository for Words.
 *
 * @see com.viktorban.wlgame.model.Word
 */
@PreAuthorize("hasAuthority('PLAYER')")
public interface WordRepository extends CrudRepository<Word, Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('MODERATOR')")
    void delete(Long aLong);

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('MODERATOR')")
    void delete(Word word);

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('MODERATOR')")
    void delete(Iterable<? extends Word> iterable);

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    void deleteAll();

}
