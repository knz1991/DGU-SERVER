package com.dgu.server.service;

import com.dgu.server.model.entities.Group;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.payload.request.*;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.RoleRepository;
import com.dgu.server.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface GroupService {
    public void add(AddGroupRequest addGroupRequest) throws Exception;
    public Group findById(Long id);
    public MessageResponse addMembers(AddMembersRequest addMembersRequest, Long gid);
    public MessageResponse removeMembers(AddMembersRequest addMembersRequest, Long gid);
    public MessageResponse DeleteById(Long id) throws Exception;
    public MessageResponse Update(UpdateGroupMetaRequest updateGroupMetaRequest) throws Exception;

}
