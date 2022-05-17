package com.dgu.server.service;

import com.dgu.server.model.entities.Server;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.payload.request.AddUserRequest;
import com.dgu.server.model.payload.request.UpdateUserRequest;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.RoleRepository;
import com.dgu.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

public interface UserService {

    public void add(AddUserRequest addUserRequest, PasswordEncoder encoder, RoleRepository roleRepository, UserRepository userRepository) throws Exception;
    public User findById( Long id);
    public MessageResponse DeleteById( Long id) throws Exception;
    public MessageResponse Update(UpdateUserRequest updateUserRequest, RoleRepository roleRepository) throws Exception;
    Set<User> getOwners(Server server);
    Set<User> getNoOwners(Server server);

}
