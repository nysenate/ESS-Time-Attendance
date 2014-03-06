package gov.nysenate.seta.dao;

import gov.nysenate.seta.model.ldap.SenateLdapPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.Name;
import java.util.List;

public abstract class LdapBaseDao
{
    @Autowired
    protected LdapTemplate ldapTemplate;

    public static class DistinguishedNameMapper implements ContextMapper<Name> {
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
     * The search method on the LdapTemplate typically returns lists of Objects. In the case when a List of
     * Name objects need to be mapped to a single Name, this method will either return the Name if the list size
     * is 1, or throw an exception indicating that the list is empty or contained more than 1 Name.
     * @param nameList List<Name>
     * @return Name if nameList size is 1.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     */
    public Name getNameFromList(List<Name> nameList) throws IncorrectResultSizeDataAccessException {
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
