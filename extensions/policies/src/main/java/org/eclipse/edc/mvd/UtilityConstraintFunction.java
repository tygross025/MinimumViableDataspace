package org.eclipse.edc.mvd;

import org.eclipse.edc.identityhub.spi.credentials.model.Credential;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UtilityConstraintFunction implements AtomicConstraintFunction<Permission> {

    private static final String UTILITY_KEY = "is_utility";

    @Override
    public boolean evaluate(Operator operator, Object rightValue, Permission rule, PolicyContext context) {
        var claims = getClaims(context.getParticipantAgent().getClaims());
        switch (operator) {
            case EQ:
                return claims.contains(rightValue.toString());
            default:
                return false;
        }
    }

    private List<String> getClaims(Map<String, Object> claims) {
        return claims.values().stream()
                .filter(Credential.class::isInstance)
                .map(o -> (Credential) o)
                .map(this::getClaim)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Nullable
    private String getClaim(Credential credential) {
        var claims = credential.getCredentialSubject().getClaims();
        var o = claims.get(UTILITY_KEY);
        return o instanceof String ? (String) o : null;
    }
}
