package gov.nysenate.seta.dao;

import gov.nysenate.seta.AbstractContextTests;
import gov.nysenate.seta.model.ldap.SenateLdapPerson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NameNotFoundException;

import javax.naming.Name;
import javax.naming.ldap.LdapName;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class SenateLdapDaoTests extends AbstractContextTests
{
    @Autowired
    SenateLdapDao senateLdapDao;

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
        assertNotNull(senateLdapDao);
    }

    /** {@link SenateLdapDao#getPerson(javax.naming.Name)} (String)} tests
     * -------------------------------------------------------------------- */

    @Test
    public void testGetPerson() throws Exception {
        Name name = new LdapName(validDn);
        SenateLdapPerson person = senateLdapDao.getPerson(name);
        assertNotNull(person);
        assertEquals(validSenateLdapPerson.getUid(), person.getUid());
    }

    @Test(expected = NameNotFoundException.class)
    public void testGetPersonThrowsNameNotFoundException() throws Exception {
        SenateLdapPerson person = senateLdapDao.getPerson(invalidLdapName);
    }

    /** {@link SenateLdapDao#getDnFromUserId(String)} tests
     * -------------------------------------------------------- */

    @Test
    public void testGetDnFromUserIdSucceeds() throws Exception {
        Name name = senateLdapDao.getDnFromUserId(validUid);
        assertEquals(validDn.toLowerCase(), name.toString().toLowerCase());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testGetDnFromUserIdWithInvalidUid_throwsEmptyResultException() throws Exception {
        String invalidUid = "moosekitten";
        Name name = senateLdapDao.getDnFromUserId(invalidUid);
    }

    /** {@link SenateLdapDao#authenticateByUid(String, String)} tests
     * ------------------------------------------------------------------*/

    @Test
    public void testAuthenticateByUidSucceeds() throws Exception {
        Name name = senateLdapDao.authenticateByUid(validUid, validPassword);
        assertNotNull(name);
        assertEquals(validDn.toLowerCase(), name.toString().toLowerCase());
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateByUidWithWrongPassword_throwsAuthenticationException() throws Exception {
        senateLdapDao.authenticateByUid(validUid, "clearlyAWrongPassword");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateByUidWithWrongUidAndPassword_throwsAuthenticationException() throws Exception {
        senateLdapDao.authenticateByUid(invalidUid, "clearlyAWrongPassword");
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateByUidWithNullUidAndPassword_throwsAuthenticationException() throws Exception {
        senateLdapDao.authenticateByUid(null, null);
    }
}
