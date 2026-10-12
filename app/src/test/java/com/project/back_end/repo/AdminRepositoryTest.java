package com.project.back_end.repo;

import com.project.back_end.models.Admin;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Value("${repository.tests.enabled:false}")
    private boolean isRepositoryTestsEnabled;

    /**
     * Testing AdminRepository  Custom Query Methods
     */
    @Test
    @DisplayName("Should find admin user by username when user exists")
    void shouldFindByUsernameWhenUserExists() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");

        Admin admin = adminRepository.findByUsername("Admin");
        // Then: Assert result
        Assertions.assertNotNull(admin);
        assertThat(admin.getUsername()).isEqualTo("admin");
        assertThat(admin.getPassword()).isEqualTo("admin@1234");
        System.out.println("AdminRepositoryTest.shouldFindByUsernameWhenUserExists - Should find admin user by username when user exists" + " - OK");
    }

    @Test
    @DisplayName("Should find admin user by Id when user exists")
    @Transactional /* Rolls back at the end */
    void shouldFindUserByIDWhenUserExists() {
        assumeTrue(isRepositoryTestsEnabled, "Skipping test if repository.tests.enabled.property is false");

        Admin admin = new Admin("admin2", "admin2-pass");
        Admin result = adminRepository.saveAndFlush(admin);
        // Then: Assert result
        Assertions.assertNotNull(result);
        assertThat(result.getUsername()).isEqualTo("admin2");
        assertThat(result.getPassword()).isEqualTo("admin2-pass");
        //
        Optional<Admin> newAdmin = adminRepository.findById(result.getId());
        assertThat(newAdmin.isPresent()).isTrue();
        assertThat(newAdmin.orElseThrow().getUsername()).isEqualTo("admin2");
        System.out.println("AdminRepositoryTest.shouldFindUserByIDWhenUserExists - Should find admin user by Id when user exists" + " - OK - " + result.getId());
    }
}