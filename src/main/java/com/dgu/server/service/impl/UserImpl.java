package com.dgu.server.service.impl;

import com.dgu.server.model.entities.Script;
import com.dgu.server.model.entities.Server;
import com.dgu.server.model.enums.ERole;
import com.dgu.server.model.entities.Role;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.payload.request.AddUserRequest;
import com.dgu.server.model.payload.request.UpdateUserRequest;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.RoleRepository;
import com.dgu.server.repository.ScriptRepository;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    //TODO IMPLEMENT A DELETE METHOD TO TAKE IN ACCOUNT SCRIPTS THAT WILL NO LONGER HAVE AN AUTHOR IN THE DB

    @Override
    public void add(AddUserRequest addUserRequest, PasswordEncoder encoder, RoleRepository roleRepository, UserRepository userRepository) throws Exception {
        User user = new User(addUserRequest.getEmail(),
                encoder.encode(addUserRequest.getPassword()),addUserRequest.getTrigramme(), true
        );
        Set<String> strRoles = addUserRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    //TODO MAKE USER "INACTIVE" INSTEAD OF WIPING IT FROM THE DATABASE
    @Override
    public MessageResponse DeleteById(Long id) {
        User user = this.findById(id);
        if(user==null)
            return new MessageResponse("User doesn't exist!");
        try{
            Set<Script> sharedScripts = user.getScripts();
            sharedScripts.clear();
            Set<Script> ownedScripts = user.getOwnedscripts();
            ownedScripts.stream().map(script ->{
                script.setAuthor(null);
                this.scriptRepository.save(script);
                return script;
            }).collect(Collectors.toSet());
            ownedScripts.clear();
            user.setScripts(sharedScripts);
            user.setOwnedscripts(ownedScripts);
            userRepository.save(user);
            userRepository.deleteById(id);
            return new MessageResponse("Delete Succeded: User n°"+id+" has been deleted!");
        }
        catch(Exception e){
            throw e;
        }
    }

    public MessageResponse Update(UpdateUserRequest updateUserRequest, RoleRepository roleRepository) throws Exception{
        Optional<User> updatedUser = userRepository.findById(updateUserRequest.getId());
        Set<String> strRoles = updateUserRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        updatedUser.get().setRoles(roles);
        switch(updateUserRequest.getApproved()){
            case "null":
                updatedUser.get().setApproved(null);;
                break;
            case "approved":
                updatedUser.get().setApproved(true);
                break;
            default:
                updatedUser.get().setApproved(false);
        };
        userRepository.save(updatedUser.get());
        return new MessageResponse("Update Succeded: User n°"+updatedUser.get().getId()+" has been updated!");
    }

    @java.lang.Override
    public Set<User> getOwners(Server server) {
        return userRepository.getByServersContains(server);
    }

    @java.lang.Override
    public Set<User> getNoOwners(Server server) {
        return userRepository.getByServersNotContaining(server);
    }

}
