package com.viktorban.wlgame.utility;

import com.viktorban.wlgame.model.User;
import com.viktorban.wlgame.model.UserWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Custom UserDetailsService implementation to use User entities for authentication.
 *
 * @see com.viktorban.wlgame.model.User
 * @see com.viktorban.wlgame.model.UserWrapper
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * JPA entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = (User)entityManager
                    .createQuery("SELECT u FROM com.viktorban.wlgame.model.User u WHERE u.name = :username")
                    .setParameter("username", username)
                    .getSingleResult();
            return new UserWrapper(user);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Username not found.", e);
        }
    }

}
