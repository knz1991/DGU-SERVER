package com.dgu.server.service;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.dgu.server.model.entities.User;
import com.dgu.server.model.payload.request.ExecuteScriptRequest;
import com.dgu.server.model.payload.request.ShareScriptRequest;
import com.dgu.server.model.payload.request.UpdateScriptRequest;
import com.dgu.server.model.payload.response.ScriptDetailsResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ScriptService {
    void saveScript(MultipartFile scriptFile, String description, String editable, Long user_id, String type);
    List<ScriptDetailsResponse> getPersonalScripts(Long id);
    Resource loadScript(String filename);
    List<ScriptDetailsResponse> getAllScripts();
    void deleteById(Long id) throws IOException;
    void updateScript(UpdateScriptRequest updateScriptRequest, Optional<User> user) throws IOException;
    String executeScript(Long id, ExecuteScriptRequest executeScriptRequest) throws Exception;
    void shareScript(Long sId, ShareScriptRequest shareScriptRequest, String token);
    void removeScript(Long sId, Long uId);

}
