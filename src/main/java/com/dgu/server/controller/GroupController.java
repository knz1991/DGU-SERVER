package com.dgu.server.controller;
import com.dgu.server.model.entities.Group;
import com.dgu.server.model.payload.request.AddGroupRequest;
import com.dgu.server.model.payload.request.AddMembersRequest;
import com.dgu.server.model.payload.request.UpdateGroupMetaRequest;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.GroupRepository;
import com.dgu.server.service.impl.GroupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired
    private GroupServiceImpl groupService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    PasswordEncoder encoder;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        /**
         * System.out.println("Groups: ");
         * groupRepository.findAll().stream().map(group -> {
         *             System.out.println("group n°"+group.getId()+": "+group.getName());
         *             return group;
         *         }).collect(Collectors.toList());
         */
        return ResponseEntity.ok(groupRepository.findAll());
    }

    @PostMapping("")
    public ResponseEntity<?> add(@RequestBody AddGroupRequest addGroupRequest) {
        if (groupRepository.existsByName(addGroupRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Group name is already taken!"));
        }

        // Create new user's
        try {
            this.groupRepository.save(new Group(addGroupRequest.getName(), addGroupRequest.getDescription()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Couldn't save group! \n"+e.getMessage()));
        }
        return ResponseEntity.ok(new MessageResponse("Group Added Successfully!"));
    }

    @PutMapping("")
    public ResponseEntity<?> update(@RequestBody UpdateGroupMetaRequest updateGroupMetaRequest) {
        if (!groupRepository.existsById(updateGroupMetaRequest.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Group doesn't exist!"));
        }
        try {
            return ResponseEntity.accepted().body(groupService.Update(updateGroupMetaRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> addMember(@PathVariable("id") Long id, @RequestBody AddMembersRequest addMembersRequest){
        try {
            return ResponseEntity.ok(groupService.addMembers(addMembersRequest, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> removeMember(@PathVariable("id") Long id, @RequestBody AddMembersRequest addMembersRequest){
        try {
            return ResponseEntity.ok(groupService.removeMembers(addMembersRequest, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(groupService.DeleteById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
