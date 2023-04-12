package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.reposotiries.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT user from User user").getResultList();
    }

    @Override
    public User show(int id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void save(User user) {
        user.setPassword((new BCryptPasswordEncoder()).encode(user.getPassword()));
        entityManager.persist(user);
    }

    @Override
    public void update(int id, User user) {

        User userToBeUpdated = show(id);
        entityManager.detach(userToBeUpdated);

        if (user.getUsername().isEmpty()) {
            userToBeUpdated.setUsername(userToBeUpdated.getUsername());
        } else {
            userToBeUpdated.setUsername(user.getUsername());
        }
        if (user.getPassword().isEmpty()) {
            userToBeUpdated.setPassword(userToBeUpdated.getPassword());
        } else {
            userToBeUpdated.setPassword((new BCryptPasswordEncoder()).encode(user.getPassword()));
        }
        if (user.getName().isEmpty()) {
            userToBeUpdated.setName(userToBeUpdated.getName());
        } else {
            userToBeUpdated.setName(user.getName());
        }
        if (user.getLastName().isEmpty()) {
            userToBeUpdated.setLastName(userToBeUpdated.getLastName());
        } else {
            userToBeUpdated.setLastName(user.getLastName());
        }
        if (user.getAge()==0) {
            userToBeUpdated.setAge(userToBeUpdated.getAge());
        } else {
            userToBeUpdated.setAge(user.getAge());
        }
        if (user.getRoles().isEmpty()) {
            userToBeUpdated.setRoles(userToBeUpdated.getRoles());
        } else {
            userToBeUpdated.setRoles(user.getRoles());
        }
        entityManager.merge(userToBeUpdated);
    }

    @Override
    public void delete(int id) {
        entityManager.remove(show(id));
    }


    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

}
