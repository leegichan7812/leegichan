package com.seowon.coding.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PermissionCheckerTest {
    private List<User> users;
    private List<UserGroup> groups;
    private List<Policy> policies;

    /**
     * ## Fixture
     *
     * | 사용자 ID | 소속 그룹                   |
     * |----------|-----------------------------|
     * | user1    | group1, group2             |
     * | user2    | group2, group3             |
     * | user4    | group4                     |
     *
     * | 그룹 ID  | 연결된 정책                 |
     * |----------|-----------------------------|
     * | group1   | policy1                    |
     * | group2   | policy2, policy3           |
     * | group3   | policy3                    |
     * | group4   | policy4                    |
     *
     * | 정책 ID  | Statement 구성                                           |
     * |----------|----------------------------------------------------------|
     * | policy1  | actions = [s3:GetObject, s3:PutObject], resources = [bucket1] |
     * | policy2  | actions = [ec2:StartInstance], resources = [instance123] |
     * | policy3  | actions = [s3:GetObject], resources = [bucket2, bucket3] |
     * | policy4  | actions = [s3:GetObject], resources = [bucket4]          |
     * |          | actions = [s3:PutObject], resources = [bucket5]          |
     */

    @BeforeEach
    void setUp() {
        users = List.of(
                new User("user1", List.of("group1", "group2")),
                new User("user2", List.of("group2", "group3")),
                new User("user4", List.of("group4"))
        );

        groups = List.of(
                new UserGroup("group1", List.of("policy1")),
                new UserGroup("group2", List.of("policy2", "policy3")),
                new UserGroup("group3", List.of("policy3")),
                new UserGroup("group4", List.of("policy4"))
        );

        policies = List.of(
                new Policy("policy1", List.of(
                        Statement.builder()
                                .actions(List.of("s3:GetObject", "s3:PutObject"))
                                .resources(List.of("bucket1")).build()
                )),
                new Policy("policy2", List.of(
                        Statement.builder()
                                .actions(List.of("ec2:StartInstance"))
                                .resources(List.of("instance123")).build()
                )),
                new Policy("policy3", List.of(
                        Statement.builder()
                                .actions(List.of("s3:GetObject"))
                                .resources(List.of("bucket2", "bucket3")).build()
                )),
                new Policy("policy4", List.of( // 복수의 Statement를 가진 정책 추가
                        Statement.builder()
                                .actions(List.of("s3:GetObject"))
                                .resources(List.of("bucket4")).build(),
                        Statement.builder()
                                .actions(List.of("s3:PutObject"))
                                .resources(List.of("bucket5")).build()
                ))
        );
    }


    @DisplayName("사용자에게 권한이 있으면 true를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnTrue_WhenUserHasPermission() {
        boolean result = PermissionChecker.hasPermission(
                "user1",
                "bucket1",
                "s3:GetObject",
                users,
                groups,
                policies
        );
        assertTrue(result, "user1은 bucket1에서 s3:GetObject를 수행할 수 있는 권한이 있어야 합니다.");
    }

    @DisplayName("권한 있음은 사용자에게 권한이 없을 때 false를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnFalse_WhenUserDoesNotHavePermission() {
        boolean result = PermissionChecker.hasPermission(
                "user1",
                "bucket3",
                "s3:PutObject",
                users,
                groups,
                policies
        );
        assertFalse(result, "user1은 bucket3에서 s3:PutObject를 수행할 권한이 없어야 합니다.");
    }

    @DisplayName("권한이 다른 그룹에서 온 경우 true를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnTrue_WhenPermissionIsFromAnotherGroup() {
        boolean result = PermissionChecker.hasPermission(
                "user2",
                "bucket2",
                "s3:GetObject",
                users,
                groups,
                policies
        );
        assertTrue(result, "user2 should have permission to perform s3:GetObject on bucket2");
    }

    @DisplayName("권한이 있으면 사용자가 존재하지 않으면 false를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnFalse_WhenUserDoesNotExist() {
        boolean result = PermissionChecker.hasPermission(
                "nonexistentUser",
                "bucket1",
                "s3:GetObject",
                users,
                groups,
                policies
        );
        assertFalse(result, "Nonexistent user should not have any permissions");
    }

    @DisplayName("사용자에게 그룹이 없으면 권한이 있음이 false를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnFalse_WhenUserHasNoGroups() {
        boolean result = PermissionChecker.hasPermission(
                "user3",
                "bucket1",
                "s3:GetObject",
                users,
                groups,
                policies
        );
        assertFalse(result, "user3에는 그룹이 없으며 권한이 없어야 합니다.");
    }

    @DisplayName("일치하는 정책이 없으면 권한이 false를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnFalse_WhenNoMatchingPolicyExists() {
        boolean result = PermissionChecker.hasPermission(
                "user1",
                "nonexistentBucket",
                "s3:GetObject",
                users,
                groups,
                policies
        );
        assertFalse(result, "user1은 존재하지 않는 리소스에 대한 권한을 가져서는 안 됩니다.");
    }

    @DisplayName("권한이 있으면 여러 작업 및 리소스에 대해 true를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnTrue_ForMultipleActionsAndResources() {
        boolean result = PermissionChecker.hasPermission(
                "user1",
                "bucket1",
                "s3:PutObject",
                users,
                groups,
                policies
        );
        assertTrue(result, "user1은 bucket1에서 s3:PutObject를 수행할 수 있는 권한이 있어야 합니다.");
    }

    @DisplayName("정책이 여러 Statement를 포함하고 있을 때 권한이 있으면 true를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnTrue_WhenPolicyHasMultipleStatements() {
        boolean result1 = PermissionChecker.hasPermission(
                "user4",
                "bucket4",
                "s3:GetObject",
                users,
                groups,
                policies
        );
        assertTrue(result1, "user4는 bucket4에서 s3:GetObject를 수행할 수 있는 권한이 있어야 합니다.");

        boolean result2 = PermissionChecker.hasPermission(
                "user4",
                "bucket5",
                "s3:PutObject",
                users,
                groups,
                policies
        );
        assertTrue(result2, "user4는 bucket5에서 s3:PutObject를 수행할 수 있는 권한이 있어야 합니다.");
    }

    @DisplayName("정책에 여러 Statement가 있어도 권한이 없으면 false를 반환해야 합니다.")
    @Test
    void hasPermission_ShouldReturnFalse_WhenPolicyHasMultipleStatementsButNoPermission() {
        boolean result = PermissionChecker.hasPermission(
                "user1",
                "bucket6",
                "s3:DeleteObject",
                users,
                groups,
                policies
        );
        assertFalse(result, "user1은 bucket6에서 s3:DeleteObject를 수행할 권한이 없어야 합니다.");
    }
}