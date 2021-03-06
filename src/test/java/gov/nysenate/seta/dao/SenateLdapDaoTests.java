package gov.nysenate.seta.dao;

import gov.nysenate.seta.BaseTests;
import gov.nysenate.seta.dao.personnel.LdapAuthDao;
import gov.nysenate.seta.model.auth.SenateLdapPerson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NameNotFoundException;

import javax.naming.Name;
import javax.naming.ldap.LdapName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SenateLdapDaoTests extends BaseTests
{
    @Autowired
    LdapAuthDao ldapAuthDao;

    @Value("${test.ldap.valid.uid}") private String validUid;
    @Value("${test.ldap.valid.dn}") private String validDn;
    @Value("${test.ldap.valid.password}") private String validPassword;

    private SenateLdapPerson validSenateLdapPerson = new SenateLdapPerson();
    private LdapName invalidLdapName;
    private String invalidUid;

    @Before
    public void setUp() throws Exception {
        validSenateLdapPerson.setUid(validUid);
        invalidLdapName = new LdapName("CN=moosekitten,O=senate");
        invalidUid = "moosekitten";
    }

    @Test
    public void testSenateLdapDaoAutowired() throws Exception {
        assertNotNull(ldapAuthDao);
    }

    /** {@link LdapAuthDao#getPerson(javax.naming.Name)} (String)} tests
     * -------------------------------------------------------------------- */

    @Test
    public void testGetPerson() throws Exception {
        Name name = new LdapName(validDn);
        SenateLdapPerson person = ldapAuthDao.getPerson(name);
        assertNotNull(person);
        assertEquals(validSenateLdapPerson.getUid(), person.getUid());
    }

    @Test(expected = NameNotFoundException.class)
    public void testGetPersonThrowsNameNotFoundException() throws Exception {
        SenateLdapPerson person = ldapAuthDao.getPerson(invalidLdapName);
    }

    /** {@link LdapAuthDao#authenticateByUid(String, String)} tests
     * ------------------------------------------------------------------*/

    @Test
    public void testAuthenticateByUidSucceeds() throws Exception {
        Name name = ldapAuthDao.authenticateByUid(validUid, validPassword);
        assertNotNull(name);
        assertEquals(validDn.toLowerCase(), name.toString().toLowerCase());
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateByUidWithWrongPassword_throwsAuthenticationException() throws Exception {
        ldapAuthDao.authenticateByUid(validUid, "clearlyAWrongPassword");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateByUidWithWrongUidAndPassword_throwsAuthenticationException() throws Exception {
        ldapAuthDao.authenticateByUid(invalidUid, "clearlyAWrongPassword");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateByUidWithNullUidAndPassword_throwsAuthenticationException() throws Exception {
        ldapAuthDao.authenticateByUid(null, null);
    }
}
