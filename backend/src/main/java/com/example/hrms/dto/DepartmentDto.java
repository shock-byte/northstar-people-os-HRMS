package com.example.hrms.dto;

import com.example.hrms.model.Department;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentDto {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String code;

    @Size(max = 255)
    private String description;

    @Size(max = 150)
    private String managerName;

    public static DepartmentDto fromEntity(Department entity) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setManagerName(entity.getManagerName());
        return dto;
    }

    public void updateEntity(Department entity) {
        entity.setName(this.name);
        entity.setCode(this.code);
        entity.setDescription(this.description);
        entity.setManagerName(this.managerName);
    }
}

