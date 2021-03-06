package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Roles;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RolesDaoTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    @Before
    public void init() {
        initDB();
    }

    @After
    public void stop() throws SQLException {
        //TODO
    }

    @Inject
    private RolesDAO dao;

    @Test
    public void testSave() {
        final Roles item = new Roles("admin", "all admin functions");
        List<Roles> list = null;
        try {
            dao.save(item);
            list = dao.findAll();

        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error testing saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 1);
        final Roles role = list.get(0);
        assertEquals("Value mismatch", role.getRoleName(), "admin");
    }

}