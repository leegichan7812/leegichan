package com.seowon.coding.domain.model;


import lombok.Builder;

import java.util.List;

class PermissionChecker {

    /**
     * TODO #7: 코드를 최적화하세요
     * 테스트 코드`PermissionCheckerTest`를 활용하시면 작업 결과를 검증 할 수 있습니다.
     */
    public static boolean hasPermission(
            String userId,
            String targetResource,
            String targetAction,
            List<User> users,
            List<UserGroup> groups,
            List<Policy> policies
    ) {
        for (User user : users) {
            if (user.id.equals(userId)) {
                for (String groupId : user.groupIds) {
                    for (UserGroup group : groups) {
                        if (group.id.equals(groupId)) {
                            for (String policyId : group.policyIds) {
                                for (Policy policy : policies) {
                                    if (policy.id.equals(policyId)) {
                                        for (Statement statement : policy.statements) {
                                            if (statement.actions.contains(targetAction) &&
                                                statement.resources.contains(targetResource)) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}

class User {
    String id;
    List<String> groupIds;

    public User(String id, List<String> groupIds) {
        this.id = id;
        this.groupIds = groupIds;
    }
}

class UserGroup {
    String id;
    List<String> policyIds;

    public UserGroup(String id, List<String> policyIds) {
        this.id = id;
        this.policyIds = policyIds;
    }
}

class Policy {
    String id;
    List<Statement> statements;

    public Policy(String id, List<Statement> statements) {
        this.id = id;
        this.statements = statements;
    }
}

class Statement {
    List<String> actions;
    List<String> resources;

    @Builder
    public Statement(List<String> actions, List<String> resources) {
        this.actions = actions;
        this.resources = resources;
    }
}