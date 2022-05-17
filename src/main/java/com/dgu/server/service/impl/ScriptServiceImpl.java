package com.dgu.server.service.impl;

import com.dgu.server.applicationConfig.jwt.JwtUtils;
import com.dgu.server.model.entities.*;
import com.dgu.server.model.enums.EType;
import com.dgu.server.model.payload.request.ExecuteScriptRequest;
import com.dgu.server.model.payload.request.ShareScriptRequest;
import com.dgu.server.model.payload.request.UpdateScriptRequest;
import com.dgu.server.model.payload.response.ScriptDetailsResponse;
import com.dgu.server.repository.ScriptRepository;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.ScriptService;
import com.dgu.server.service.ServerService;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ScriptServiceImpl implements ScriptService {
    private final Path root = Paths.get("scripts");

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScriptRepository scriptRepository;

    @Autowired
    ServerImpl serverService;

    @Autowired
    ExecutionServiceImpl executionService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    JwtUtils jwtUtils;


    @Override
    public void saveScript(MultipartFile scriptFile, String description, String editable, Long userId, String strType) {
        try {
            if(!scriptFile.getContentType().equals("text/x-sh"))
                throw new RuntimeException("File is not a Shell script");
            Files.copy(scriptFile.getInputStream(), this.root.resolve(scriptFile.getOriginalFilename()));
        }
        catch(IOException e){
            throw new RuntimeException("File with script name "+scriptFile.getOriginalFilename()+" already exists!");
        }
        catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
        System.out.println("File Saved Successfully");
        try {
            String src = "scripts/"+scriptFile.getOriginalFilename();
            boolean edit = false;
            if(editable.equals("true"))
                edit=true;
            System.out.println("Editable: "+editable+" edit: "+edit);
            EType type;
            if(strType.equals("Private")){
                type= EType.Private;
            }
            else type = EType.Public;
            Script script = new Script(scriptFile.getOriginalFilename(),
                    description,
                    src,
                    edit,
                    type);

            Optional<User> owner = userRepository.findById(userId);
            if(owner.isPresent()){
                script.setAuthor(owner.get());
                script.setModifier(owner.get());
                this.scriptRepository.save(script);
                Set<Script> scripts =  owner.get().getScripts();
                scripts.add(script);
                owner.get().setScripts(scripts);
                userRepository.save(owner.get());
            }
        } catch(Exception e) {
            throw new RuntimeException("Error in saving the script in the DB:\n "+e.getMessage());
        }

    }

    @Override
    public List<ScriptDetailsResponse> getAllScripts() {
        List<Script> allscripts;
        try {
            allscripts = this.scriptRepository.findAll();
        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return getScriptDetails(allscripts);
    }

    @Override
    public List<ScriptDetailsResponse> getPersonalScripts(Long id) {
        try  {
            Optional<User> user = userRepository.findById(id);
            if(!user.isPresent())
                throw new RuntimeException("User doesn't exist");
            return this.getScriptDetails(user.get().getScripts().stream().toList());
        }
        catch(RuntimeException e){
            throw e;
        }

    }

    public List<ScriptDetailsResponse> getScriptDetails(List<Script> scripts){
        return scripts.stream()
                .map(script ->{
                            String[] splitSrc = script.getSrc().split("[/]");
                            String filename = splitSrc[1];
                            String content = asString(this.loadScript(filename));
                            String author="";
                            String modifier="";
                            if(script.getAuthor()==null)
                                author = "Deleted";
                            else author = script.getAuthor().getTrigramme();
                            if(script.getModifier()==null)
                                modifier = "Deleted";
                            else modifier = script.getModifier().getTrigramme();
                            return new ScriptDetailsResponse(script.isEditable(),content,script.getName(),script.getDescription(),script.getId(),script.getType(),author, modifier, script.getCreatedAt(), script.getLastModifiedAt());
                        }
                ).collect(Collectors.toList());
    }

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Resource loadScript(String filename) {
        try {
            Path file = root.resolve(filename);
            //System.out.println("FILE URI: "+file.toUri());
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() ) {
                return resource;
            } else {
                throw new RuntimeException("Could not read "+filename+" !");
            }
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
        catch (RuntimeException e){
            throw e;
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public void deleteById(Long id) throws IOException {
        Optional<Script> script = scriptRepository.findById(id);
        try{
            if(script.isPresent()){
                Set<User> users = script.get().getShared();
                users = users.stream().map(user ->{
                    Set<Script> scripts = user.getScripts();
                    scripts.remove(script.get());
                    user.setScripts(scripts);
                    return user;
                }).collect(Collectors.toSet());
                userRepository.saveAll(users);
                Files.delete(Path.of(script.get().getSrc()));
            }
            else throw new RuntimeException("Error: Script with id°"+id+" doesn't exist!");
            scriptRepository.deleteById(id);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error: Something went wrong!");
        }
    }

    @Override
    public void updateScript(UpdateScriptRequest updateScriptRequest, Optional<User> optionalUser) throws IOException {
        int role_user = optionalUser.get().getRoles().stream()
                .map(Role::getId).toList().get(0);
        boolean cd = false;
        Optional<Script> script = scriptRepository.findById(updateScriptRequest.getId());
        if(script.isPresent()) {
            if(script.get().isEditable()) {
                if(script.get().getShared().stream().map(User::getId).toList().contains(optionalUser.get().getId()))
                    cd = true;
            }
            else if(role_user ==2)
                cd = true;
            if(script.get().getAuthor()!=null)
                if(script.get().getAuthor().getId().equals(optionalUser.get().getId()))
                    cd = true;
            if(!cd)
                throw new RuntimeException("User is not authorized to update script n°"+updateScriptRequest.getId());
            switch (updateScriptRequest.getUpdate()) {
                case "uneditable" -> {
                    script.get().setEditable(false);
                    script.get().setModifier(optionalUser.get());
                    script.get().setLastModifiedAt(new Date());
                    scriptRepository.save(script.get());
                }
                case "editable" -> {
                    script.get().setEditable(true);
                    script.get().setModifier(optionalUser.get());
                    script.get().setLastModifiedAt(new Date());
                    scriptRepository.save(script.get());
                }
                case "public" -> {
                    script.get().setType(EType.Public);
                    script.get().setModifier(optionalUser.get());
                    script.get().setLastModifiedAt(new Date());
                    scriptRepository.save(script.get());
                }
                case "private" -> {
                    script.get().setType(EType.Private);
                    script.get().setModifier(optionalUser.get());
                    script.get().setLastModifiedAt(new Date());
                    scriptRepository.save(script.get());
                }
                default -> {
                    //System.out.println("UPDATING SCRIPT CONTENT:\n "+updateScriptRequest.getCode());
                    Files.writeString(Path.of(script.get().getSrc()), updateScriptRequest.getCode());
                    script.get().setDescription(updateScriptRequest.getDescription());
                    script.get().setName(updateScriptRequest.getName());
                    script.get().setEditable(updateScriptRequest.isEditable());
                    if (updateScriptRequest.getType().equals("Private"))
                        script.get().setType(EType.Private);
                    else script.get().setType(EType.Public);
                    script.get().setModifier(optionalUser.get());
                    script.get().setLastModifiedAt(new Date());
                    scriptRepository.save(script.get());
                }
            }
        }
        else throw new RuntimeException("Script doesn't exist!");
    }

    @Override
    public String executeScript(Long id, ExecuteScriptRequest executeScriptRequest) throws Exception {
        Optional<Script> script = Optional.of(this.scriptRepository.getById(executeScriptRequest.getScriptId()));
        String src = "";
        if(script.isPresent())
            src = script.get().getSrc();
        else throw new RuntimeException("Script doesn't or no longer exists!");
        //System.out.println("SCRIPT SRC: "+src);
        Optional<Server> serverOptional = Optional.of(this.serverService.getServerById(executeScriptRequest.getServerId()));
        if(!serverOptional.isPresent())
            throw new RuntimeException("Server doesn't exist!");
        String serverIp = serverOptional.get().getServerIp();
        System.out.println("File upload: "+this.uploadSftpFromPath(executeScriptRequest.getUser(),executeScriptRequest.getPassword(),
                serverIp,executeScriptRequest.getPort(),src));
        System.out.println("COMMAND: bash remoteScript.sh"+executeScriptRequest.getArgs());
        List<String> resultList = this.executeCommand(executeScriptRequest.getUser(), executeScriptRequest.getPassword(),
                serverIp, executeScriptRequest.getPort(), "bash remoteScript.sh "+executeScriptRequest.getArgs(), true, id);
        //Create execution based on the execution exit status (resultList index 0) and SSH response stream (resultList index 1)
        String result = resultList.get(0).equals("0") ? "Success" : "Failure";
        String strPort = ""+executeScriptRequest.getPort();
        Execution execution = new Execution(userRepository.getById(executeScriptRequest.getExecutorId()),scriptRepository.getById(executeScriptRequest.getScriptId()),serverService.getServerById(executeScriptRequest.getServerId()),result,resultList.get(0),resultList.get(1),executeScriptRequest.getUser(),strPort,String.join(" ",executeScriptRequest.getArgs()));
        this.executionService.createExecution(execution);
        System.out.println("Script Execution:\n"+ resultList);
        System.out.println("Script Deleted!"+executeCommand(executeScriptRequest.getUser(),executeScriptRequest.getPassword(),
                serverIp,executeScriptRequest.getPort(),"rm remoteScript.sh",false,id));
        return "Exit Status: "+resultList.get(0);
    }

    @Override
    public void shareScript(Long sId, ShareScriptRequest shareScriptRequest, String token) {
        String email = this.jwtUtils.getEmailFromJwtToken(token.substring(7));
        Optional<User> user = this.userRepository.findByEmail(email);
        int role_user = user.get().getRoles().stream()
                .map(Role::getId).toList().get(0);
        boolean cd = false;
        Optional<Script> script = scriptRepository.findById(sId);
        if(script.isPresent()) {
            if (role_user == 2)
                cd = true;
            else if (script.get().getType().equals(EType.Public))
                cd = true;
            if (!cd)
                throw new RuntimeException("User is not authorized to add script n°" + sId);
        }
        if(!script.isPresent())
            throw new RuntimeException("Script n°"+sId+" doesn't exist!");
        Set<User> sharedUsers = script.get().getShared();
        Optional<User> user1 = this.userRepository.findById(shareScriptRequest.getUId());
        if(!user1.isPresent())
            throw new RuntimeException("User n°"+shareScriptRequest.getUId()+" doesn't exist!");
        if(shareScriptRequest.isShare()){
            sharedUsers.add(user1.get());
            script.get().setShared(sharedUsers);
            Set<Script> sharedScripts = user1.get().getScripts();
            sharedScripts.add(script.get());
            user1.get().setScripts(sharedScripts);
        }
        else {
            sharedUsers.remove(user1.get());
            script.get().setShared(sharedUsers);
            Set<Script> sharedScripts = user1.get().getScripts();
            sharedScripts.remove(script.get());
            user1.get().setScripts(sharedScripts);
        }
        try{
            this.userRepository.save(user.get());
        }
        catch(Exception e){
            throw new RuntimeException("Something went wrong with sharing script n°"+sId+" with user n°"+shareScriptRequest.getUId()+" !");
        }
    }

    @Override
    public void removeScript(Long sId, Long uId) {
        Optional<Script> script = this.scriptRepository.findById(sId);
        Optional<User> user = this.userRepository.findById(uId);
        if(!user.isPresent())
            throw new RuntimeException("User n°"+uId+" doesn't exist!");
        if(!script.isPresent())
            throw new RuntimeException("Script n°"+sId+" doesn't exist!");
        if(!user.get().getScripts().contains(script.get()))
            throw new RuntimeException("User n°"+uId+" does not have script n°"+sId+" in his share scripts list!");
        Set<Script> sharedScripts = user.get().getScripts();
        sharedScripts.remove(script.get());
        user.get().setScripts(sharedScripts);
        this.userRepository.save(user.get());
    }

    public List<String> executeCommand(String username, String password,
                                 String host, int port, String command, Boolean send, Long id) throws Exception {
        ChannelExec channel = null;
        String resultString = "";
        List<String> results = new ArrayList<>();
        try {
            channel = (ChannelExec) setupJsch(username,password,host,port, "exec");
            //System.out.println("test 1: Connection to session Established!");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.setErrStream(responseStream);
            channel.connect();
            System.out.println("Connected to Channel!");
            while (channel.isConnected()) {
                Thread.sleep(100);
                if(send) {
                    this.messagingTemplate
                            .convertAndSend("/script/execution/"+id, responseStream.toString());
                    //System.out.println("MESSAGE SENT TO: "+"/script/execution");
                }
            }
            resultString = "Script Executed with Exit Status: "+channel.getExitStatus();
            System.out.println("Exit Status: "+channel.getExitStatus());
            String exitStatus = ""+channel.getExitStatus();
            results.add(exitStatus);
            results.add(responseStream.toString());

        }
        finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return results ;
        //return "done!";
    }

    private Channel setupJsch(String username, String password, String host, int port, String type) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
        return session.openChannel(type);
    }

    public boolean uploadSftpFromPath(String username, String password, String host, int port, String script) {
        ChannelSftp channelSftp = null;
        try {
            channelSftp = (ChannelSftp) setupJsch(username, password, host, port, "sftp");
            channelSftp.connect();

            try{
                channelSftp.put(script, "remoteScript.sh");
                System.out.println("Upload Complete");
            } catch (SftpException e) {
                // throw the exception
                System.out.println("Upload Failed!");
                e.printStackTrace();
            }
        } catch (JSchException e) {
            // throw the exception
            System.out.println("Session/Channel connection Failed!");
            e.printStackTrace();
            throw new RuntimeException("Session/Channel connection to Host Failed!");
        } finally {
            if (channelSftp != null)
                channelSftp.disconnect();
        }
        return true;
    }


}
