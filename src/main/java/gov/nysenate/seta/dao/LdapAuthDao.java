package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.ldap.SenateLdapPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.*;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
import javax.naming.directory.DirContext;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Provides access to LDAP functionality for authenticating users and retrieving data model
 * objects that represent LDAP records.
 */
@Repository
public class LdapAuthDao extends LdapBaseDao
{
   /**
     * Authenticates uid via a simple LDAP bind. If the bind was successful the matching Name will be returned.
     * Otherwise an error will have been thrown indicating the exception.
     * @param uid String (username)
     * @param credentials String (password)
     * @return Name representing authenticated user. Otherwise exception will be thrown.
     * @throws AuthenticationException given invalid credentials
     * @throws NamingException general exception
     * @throws IncorrectResultSizeDataAccessException potential multiple matches
   */
    public Name authenticateByUid(String uid, String credentials) throws NamingException,
                                                                         IncorrectResultSizeDataAccessException {
        return ldapTemplate.authenticate(query().where("uid").is(uid), credentials, new AuthenticatedLdapEntryContextMapper<Name>() {
            @Override
            public Name mapWithContext(DirContext ctx, LdapEntryIdentification ldapEntryIdentification) {
                return ldapEntryIdentification.getAbsoluteName();
            }
        });
    }
}