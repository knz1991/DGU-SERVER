package com.dgu.server.service;
import com.dgu.server.model.entities.Server;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.payload.response.MessageResponse;

import java.util.List;
import java.util.Set;


public interface ServerService {


    Set<Server> getServer();

    Server getServerById(Long id);

    Server getByName(String name);


    Server insert(Server server);

    void updateServer(Server server);

    public MessageResponse deleteById(Long id) throws Exception;

    Set<Server> getserversbyowner (User user);

    Set<Server> getPublicServers();

    Server insertserversowner (User user, Server server );

    Server deleteserversowner (User user,Server server);


}
