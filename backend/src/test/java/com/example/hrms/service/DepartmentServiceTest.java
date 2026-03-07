package com.example.hrms.service;

import com.example.hrms.dto.DepartmentDto;
import com.example.hrms.model.Department;
import com.example.hrms.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DepartmentServiceTest {

    @Test
    void findAll_mapsEntitiesToDtos() {
        DepartmentRepository repository = mock(DepartmentRepository.class);
        Department department = new Department();
        department.setId(1L);
        department.setName("IT");
        department.setCode("IT");
        when(repository.findAll()).thenReturn(List.of(department));

        DepartmentService service = new DepartmentService(repository);

        List<DepartmentDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("IT");
    }
}

