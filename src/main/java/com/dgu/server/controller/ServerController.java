package com.dgu.server.controller;

import com.dgu.server.applicationConfig.jwt.JwtUtils;
import com.dgu.server.model.entities.Role;
import com.dgu.server.model.entities.Server;
import com.dgu.server.model.entities.User;
import com.dgu.server.model.enums.EType;
import com.dgu.server.model.payload.response.MessageResponse;

import com.dgu.server.repository.UserRepository;
import com.dgu.server.service.ServerService;

import com.dgu.server.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/Server")
public class ServerController {

    @Autowired
    private ServerService serverService;
    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(serverService.getServer());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Server> getServerById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serverService.getServerById(id));
    }

    @PostMapping("")
    public ResponseEntity<Object> insertServer(@RequestBody Server server) {
        server = serverService.insert(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(" server has been inserted"));
    }

    @PutMapping("")
    public ResponseEntity<Object> updateServer(@RequestBody Server server) {
        //set server type EServer.ServerPrivate/ EServer.ServerPublic
        serverService.updateServer(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(" server has been updated "));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable("id") Long id) {
        System.out.println("delete");
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serverService.deleteById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/PerUser")
    public ResponseEntity<Object> getServersByUser(@RequestHeader("Authorization") String token) {
        String email = this.jwtUtils.getEmailFromJwtToken(token.substring(7));
        Optional<User> user = this.userRepository.findByEmail(email);
        int role_user = user.get().getRoles().stream()
                .map(Role::getId).toList().get(0);
        if (role_user == 2) {
            return ResponseEntity.status(HttpStatus.CREATED).body(serverService.getServer());
        } else {
            Set<Server> servers = serverService.getPublicServers();
            Set<Server> getServersOwned = serverService.getserversbyowner(user.get());
            servers.addAll(getServersOwned);
            return ResponseEntity.status(HttpStatus.CREATED).body(servers);
        }

    }

    @GetMapping("{id}/makePrivate")
    public ResponseEntity<Object> makePrivate(@PathVariable Long id) {
        Server server = serverService.getServerById(id);
        server.setType(EType.Private);
        serverService.updateServer(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(" server has been updated "));
    }

    @GetMapping("{id}/makePublic")
    public ResponseEntity<Object> makePublic(@PathVariable Long id) {
        Server server = serverService.getServerById(id);
        server.setType(EType.Public);
        Set<User> clean = new HashSet<>();
        server.setOwners(clean);
        serverService.updateServer(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(" server has been updated "));
    }

    @GetMapping("/{serverid}/addowner/{userid}")
    public ResponseEntity<Object> insertserversowner(@PathVariable("userid") Long userid, @PathVariable("serverid") Long serverid) {
        User user1 = userService.findById(userid);
        Server server1 = serverService.getServerById(serverid);
        serverService.insertserversowner(user1, server1);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(" server has been updated "));
    }

    @GetMapping("/{serverid}/deleteowner/{userid}")
    public ResponseEntity<Object> deleteserversowner(@PathVariable("userid") Long userid, @PathVariable("serverid") Long serverid) {
        User user1 = userService.findById(userid);
        Server server1 = serverService.getServerById(serverid);
        serverService.deleteserversowner(user1, server1);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(" server has been updated "));
    }

    @GetMapping("/Export")
    public ResponseEntity<Object> exportExcel(@RequestHeader("Authorization") String token) throws IOException {

        String email = this.jwtUtils.getEmailFromJwtToken(token.substring(7));
        Optional<User> user = this.userRepository.findByEmail(email);
        int role_user = user.get().getRoles().stream()
                .map(Role::getId).toList().get(0);
        Set<Server> servers = new HashSet<>();
        if (role_user == 2) {
            servers = serverService.getServer();
        } else {
            servers = serverService.getPublicServers();
            Set<Server> getServersOwned = serverService.getserversbyowner(user.get());
            servers.addAll(getServersOwned);
        }


        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Servers");
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 6000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("ID");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Name");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Login");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Type");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Description");
        headerCell.setCellStyle(headerStyle);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);

        int i = 0;
        for (Server s : servers) {
            Row row = sheet.createRow(i + 1);
            Cell cell = row.createCell(0);
            cell.setCellValue(s.getId());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(s.getName());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(s.getLogin());
            cell.setCellStyle(style);


            cell = row.createCell(3);
            cell.setCellValue(s.getType().toString());
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(s.getDescription());
            cell.setCellStyle(style);
            i++;

        }
        System.out.println("got here");
        File fileExcel = new File("src/main/resources/ExcelFiles/ServerTo" + user.get().getEmail() + ".xlsx");
        FileOutputStream outputStream = new FileOutputStream(fileExcel);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        System.out.println("file created");
//        fileExcel.delete();


        Resource resource = null;
        try {
            resource = new UrlResource(fileExcel.toURI());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);

        //return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse(" did it happen ? " ));
    }


}



