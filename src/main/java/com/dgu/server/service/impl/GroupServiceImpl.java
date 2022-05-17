package com.dgu.server.service.impl;

import com.dgu.server.model.entities.Group;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.payload.request.AddGroupRequest;
import com.dgu.server.model.payload.request.AddMembersRequest;
import com.dgu.server.model.payload.request.UpdateGroupMetaRequest;
import com.dgu.server.model.payload.request.UpdateUserRequest;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.GroupRepository;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Override
    public void add(AddGroupRequest addGroupRequest) throws Exception {

    }

    @Override
    public Group findById(Long id) {
        return null;
    }

    @Override
    public MessageResponse addMembers(AddMembersRequest addMembersRequest, Long gid) {
        if(!this.groupRepository.existsById(gid))
            throw new RuntimeException("Group n°"+gid+" doesn't exist!");
        Group group = this.groupRepository.getById(gid);
        Set<User> members = group.getMembers();
        addMembersRequest.getIds().stream().map(id->{
            if(!this.userRepository.existsById(id))
                throw new RuntimeException("User n°"+id+" doesn't exist!");
            User user = this.userRepository.getById(id);
            members.add(user);
            return id;
        }).collect(Collectors.toList());
        try{
            group.setMembers(members);
            this.groupRepository.save(group);
        }catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
        return new MessageResponse("Group n°"+gid+" members list updated successfully!");
    }

    @Override
    public MessageResponse removeMembers(AddMembersRequest addMembersRequest, Long gid){
        if(!this.groupRepository.existsById(gid))
            throw new RuntimeException("Group n°"+gid+" doesn't exist!");
        Group group = this.groupRepository.getById(gid);
        Set<User> members = group.getMembers();
        addMembersRequest.getIds().stream().map(id->{
            if(!this.userRepository.existsById(id))
                throw new RuntimeException("User n°"+id+" doesn't exist!");
            User user = this.userRepository.getById(id);
            members.remove(user);
            return id;
        }).collect(Collectors.toList());
        try{
            group.setMembers(members);
            this.groupRepository.save(group);
        }catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
        return new MessageResponse("Group n°"+gid+" members list updated successfully!");

    }

    @Override
    public MessageResponse DeleteById(Long id) throws Exception {
        if(!this.groupRepository.existsById(id))
            throw new RuntimeException("Group n°"+id+" doesn't exist!");
        Group group = this.groupRepository.getById(id);
        try{
            this.groupRepository.delete(group);
        }catch(Exception e)
        {
            throw new RuntimeException("Couldn't delete group n°"+id+":\n"+e.getMessage());
        }
        return new MessageResponse("Group n°"+id+" deleted successfully!");

    }

    @Override
    public MessageResponse Update(UpdateGroupMetaRequest updateGroupMetaRequest) throws Exception {
        return null;
    }
}
