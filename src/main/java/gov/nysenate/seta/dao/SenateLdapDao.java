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
public class SenateLdapDao
{
    @Autowired
    private LdapTemplate ldapTemplate;

    private static class DistinguishedNameMapper implements ContextMapper<Name> {
        @Override
        public Name mapFromContext(Object ctx) throws NamingException {
            return (ctx != null) ? ((DirContextAdapter) ctx).getDn() : null;
        }
    }

    /**
     * Maps a qualified Distinguished Name to a SenateLdapPerson object.
     * @param dn Name
     * @return SenateLdapPerson that matched the dn
     * @throws NamingException if name is not found
     */
    public SenateLdapPerson getPerson(Name dn) throws NamingException {
        return ldapTemplate.findByDn(dn, SenateLdapPerson.class);
    }

    /**
     * Given a uid value this method will return the matched record's Distinguished Name.
     * @param uid LDAP uid (username) field.
     * @return Name
     * @throws NamingException
     * @throws IncorrectResultSizeDataAccessException This is the error thrown when a single DN couldn't be matched. 
     */
    public Name getDnFromUserId(String uid) throws NamingException, IncorrectResultSizeDataAccessException {
        List<Name> dnList = ldapTemplate.search(query().where("uid").is(uid), new DistinguishedNameMapper());
        return getNameFromList(dnList);
    }

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

    /**
     * The search method on the LdapTemplate typically returns lists of Objects. In the case when a List of
     * Name objects need to be mapped to a single Name, this method will either return the Name if the list size
     * is 1, or throw an exception indicating that the list is empty or contained more than 1 Name.
     * @param nameList List<Name>
     * @return Name if nameList size is 1.
     * @throws IncorrectResultSizeDataAccessException
     */
    private Name getNameFromList(List<Name> nameList) throws IncorrectResultSizeDataAccessException {
        if (nameList != null) {
            if (nameList.size() == 1) {
                return nameList.get(0);
            }
            else if (nameList.size() > 1) {
                throw new IncorrectResultSizeDataAccessException("Failed to retrieve match based on uid. Multiple results", 1);
            }
        }
        throw new EmptyResultDataAccessException("Failed to retrieve match based on uid. No results.", 1);
    }
}