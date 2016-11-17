package com.viktorban.wlgame.controller;

import com.viktorban.wlgame.model.Word;
import org.springframework.data.repository.CrudRepository;

public interface WordRepository extends CrudRepository<Word, Long> {}
