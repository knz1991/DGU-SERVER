package com.dgu.server.model.payload.response;

import com.dgu.server.model.enums.EType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Normalized;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@AllArgsConstructor
public class ScriptDetailsResponse {
    @NotBlank
    private boolean editable;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private Long id;

    @NotBlank
    private EType type;

    @NotBlank
    private String author;

    @NotBlank
    private String modifier;

    @NotBlank
    private Date createdAt;

    @NotBlank
    private Date lastModifiedAt;

}
