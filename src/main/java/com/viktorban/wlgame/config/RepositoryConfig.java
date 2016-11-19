package com.viktorban.wlgame.config;

import com.viktorban.wlgame.model.Language;
import com.viktorban.wlgame.model.User;
import com.viktorban.wlgame.model.Word;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Configuration for Spring DATA REST repositories.
 */
@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Language.class);
        config.exposeIdsFor(Word.class);
    }

}
