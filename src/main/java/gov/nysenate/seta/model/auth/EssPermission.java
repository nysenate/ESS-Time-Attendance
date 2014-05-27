package gov.nysenate.seta.model.auth;

import org.apache.shiro.authz.Permission;

public class EssPermission implements Permission {

    @Override
    public boolean implies(Permission p) {
        return false;
    }
}
