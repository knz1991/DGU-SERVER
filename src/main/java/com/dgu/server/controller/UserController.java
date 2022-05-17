package com.dgu.server.controller;

import com.dgu.server.model.entities.Server;
import com.dgu.server.model.payload.request.AddUserRequest;
import com.dgu.server.model.payload.request.UpdateUserRequest;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.RoleRepository;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.ServerService;
import com.dgu.server.service.UserService;
import com.dgu.server.service.impl.UserImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ServerService serverService;
    @Autowired
    PasswordEncoder encoder;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("")
    public ResponseEntity<?> add(@RequestBody AddUserRequest addUserRequest) {
        if (userRepository.existsByEmail(addUserRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (userRepository.existsByTrigramme(addUserRequest.getTrigramme())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Trigram is already taken!"));
        }
        // Create new user's account
        try{
            new UserImpl().add(addUserRequest, encoder, roleRepository, userRepository);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("An error in the server has occured!"));
        }
        return ResponseEntity.ok(new MessageResponse("User Added successfully!"));
    }

    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody UpdateUserRequest updateUserRequest) {
        if (!userRepository.existsById(updateUserRequest.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User doesn't exist!"));
        }
        try{
            return ResponseEntity.accepted().body(userService.Update(updateUserRequest,roleRepository));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try{
            return ResponseEntity.ok(userService.DeleteById(id));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/ownersOfServer/{idServer}")
    public ResponseEntity<?> owners(@PathVariable("idServer") Long id) {
        Server server = serverService.getServerById(id);
        return ResponseEntity.ok(userService.getOwners(server));
    }

    @GetMapping("/notOwnersOfServer/{idServer}")
    public ResponseEntity<?> notOwners(@PathVariable("idServer") Long id) {
        Server server = serverService.getServerById(id);
        return ResponseEntity.ok(userService.getNoOwners(server));
    }

}