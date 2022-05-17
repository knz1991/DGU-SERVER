package com.dgu.server.controller;


import com.dgu.server.applicationConfig.jwt.JwtUtils;
import com.dgu.server.model.entities.Execution;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.payload.request.ExecuteScriptRequest;
import com.dgu.server.model.payload.request.ShareScriptRequest;
import com.dgu.server.model.payload.request.UpdateScriptRequest;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.ScriptRepository;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.ExecutionService;
import com.dgu.server.service.ServerService;
import com.dgu.server.service.impl.ScriptServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/scripts")
public class ScriptController {
    @Autowired
    private ScriptServiceImpl scriptService;
    @Autowired
    private ScriptRepository scriptRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExecutionService executionService;
    @Autowired
    private ServerService serverService;
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("")
    public ResponseEntity<?> getScripts(){

        try{
            return ResponseEntity.ok(scriptService.getAllScripts());
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScriptsDetails(@PathVariable("id")Long id){
        try{
            return ResponseEntity.ok(scriptService.getPersonalScripts(id));
        }
        catch(RuntimeException e){
            return ResponseEntity.internalServerError().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("description") String description,
                                                      @RequestParam("editable") String editable,
                                                      @RequestParam("user_id") Long user_id,
                                                      @RequestParam("type") String type
    ) {
        String message = "";
        try {
            scriptService.saveScript(file,description,editable,user_id,type);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
        } catch (Exception e) {
            message = "Something went wrong uploading the file " + file.getOriginalFilename() + "! \n details:"+e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> executeScript(@PathVariable("id") Long id, @RequestBody ExecuteScriptRequest executeScriptRequest){
        String resultString ="";
        try{
            resultString=this.scriptService.executeScript(id,executeScriptRequest);
        }
        catch(Exception e){
            e.printStackTrace();
            //create execution for failure
            String result = "Critical-Failure";
            String strPort = ""+executeScriptRequest.getPort();
            Execution execution = new Execution(userRepository.getById(executeScriptRequest.getExecutorId()),scriptRepository.getById(executeScriptRequest.getScriptId()),serverService.getServerById(executeScriptRequest.getServerId()),result,null,e.getMessage(),executeScriptRequest.getUser(),strPort,String.join(" ",executeScriptRequest.getArgs()));
            this.executionService.createExecution(execution);
            return ResponseEntity
                    .internalServerError()
                    .body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(new MessageResponse(resultString));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScript(@PathVariable("id") Long id){
        try {
            this.scriptService.deleteById(id);
        }
        catch (IOException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(new MessageResponse("Script successfully deleted"));
    }

    @DeleteMapping("/{uId}/{sId}")
    public ResponseEntity<?> removeScript(@PathVariable("uId") Long uId, @PathVariable("sId") Long sId){
        try{
            this.scriptService.removeScript(sId,uId);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(new MessageResponse("Script n°"+sId+" has been removed from user n°"+uId+"'s shared scripts list successfully!"));
    }

    @PutMapping("")
    public ResponseEntity<?> updateScript(@RequestHeader("Authorization") String token, @Valid @RequestBody UpdateScriptRequest updateScriptRequest){
        String email = this.jwtUtils.getEmailFromJwtToken(token.substring(7));
        Optional<User> user = this.userRepository.findByEmail(email);
        if(!user.isPresent())
            return ResponseEntity.internalServerError().body("Something is wrong with your account. Can't fetch user data with token credentials!");
        try {
            this.scriptService.updateScript(updateScriptRequest, user);
        }catch(IOException e) {
            return ResponseEntity.internalServerError().body("Server couldn't update script!");
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(new MessageResponse("Script successfully updated!"));
    }

    @PutMapping("/share/{sId}")
    public ResponseEntity<?> shareScript(@PathVariable("sId") Long sId, @RequestBody ShareScriptRequest shareScriptRequest, @RequestHeader("Authorization") String token){
        try{
            this.scriptService.shareScript(sId, shareScriptRequest, token);
        }
        catch(RuntimeException e)
        {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok(new MessageResponse("Script shared successfully!"));
    }
}
