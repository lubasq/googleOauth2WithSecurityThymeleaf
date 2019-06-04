package com.pai.pai11.dao;

import com.pai.pai11.entity.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface userDao extends CrudRepository<User, Integer> {
    
    public User findByLogin(String login);
    
    @Override
    public List< User> findAll();
    
    public void deleteByLogin(String login);
}