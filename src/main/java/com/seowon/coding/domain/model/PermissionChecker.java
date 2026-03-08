package com.seowon.coding.domain.model;

import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            List<Policy> policies) {
        if (userId == null || targetResource == null || targetAction == null) {
            return false;
        }
        if (users == null || groups == null || policies == null) {
            return false;
        }

        Map<String, User> userMap = users.stream()
                .collect(Collectors.toMap(user -> user.id, user -> user, (a, b) -> a));

        Map<String, UserGroup> groupMap = groups.stream()
                .collect(Collectors.toMap(group -> group.id, group -> group, (a, b) -> a));

        Map<String, Policy> policyMap = policies.stream()
                .collect(Collectors.toMap(policy -> policy.id, policy -> policy, (a, b) -> a));

        User user = userMap.get(userId);
        if (user == null || user.groupIds == null) {
            return false;
        }

        for (String groupId : user.groupIds) {
            UserGroup group = groupMap.get(groupId);
            if (group == null || group.policyIds == null) {
                continue;
            }

            for (String policyId : group.policyIds) {
                Policy policy = policyMap.get(policyId);
                if (policy == null || policy.statements == null) {
                    continue;
                }

                for (Statement statement : policy.statements) {
                    if (statement == null || statement.actions == null || statement.resources == null) {
                        continue;
                    }

                    if (statement.actions.contains(targetAction)
                            && statement.resources.contains(targetResource)) {
                        return true;
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