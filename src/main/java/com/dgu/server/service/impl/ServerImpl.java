package com.dgu.server.service.impl;

import com.dgu.server.model.entities.Server;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.enums.EType;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.ServerRepository;
import com.dgu.server.service.ServerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ServerImpl implements ServerService {

    @Autowired
    ServerRepository serverRepository;


    @Override
    public Set<Server> getServer() {
        return (Set<Server>) serverRepository.findAll();

    }

    @Override
    public Server getServerById(Long id) {
        return serverRepository.findById(id).get();
    }

    @Override
    public Server getByName(String name) {
        return serverRepository.findByname(name);
    }

    @Override
    public Server insert(Server server) {
        return serverRepository.save(server);
    }

    @Override
    public void updateServer(Server server) {
        Server updateServer = serverRepository.findById(server.getId()).get();
        serverRepository.save(updateServer);
    }

    @Override
    public MessageResponse deleteById(Long id) throws Exception {
        if (this.getServerById(id) == null)
            return new MessageResponse("Server doesn't exist!");
        try {
            serverRepository.deleteById(id);
            return new MessageResponse("Delete Succeded: Server n°" + id + " has been deleted!");
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Set<Server> getserversbyowner(User user) {
        return serverRepository.findByOwnersContaining(user);

    }

    @Override
    public Set<Server> getPublicServers() {
        return serverRepository.findByType(EType.Public);
    }

    @Override
    public Server insertserversowner(User user, Server server) {
        Set<User> ownersfromserver = server.getOwners();
        ownersfromserver.add(user);
        server.setOwners(ownersfromserver);
        return serverRepository.save(server);

    }
    @Override
    public Server deleteserversowner (User user,Server server) {

        if (this.getserversbyowner(user) == null)
            throw new RuntimeException(" server's owner   doesn't exist!");
        try {
            Set<User> ownersfromserver = server.getOwners();
            ownersfromserver.remove(user);
            server.setOwners(ownersfromserver);
            return serverRepository.save(server);

        } catch (Exception e) {
            throw e;
        }
    }
}








