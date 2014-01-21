package edu.yale.library.view;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import edu.yale.library.dao.UserDAO;
import edu.yale.library.dao.GenericDAO;
import edu.yale.library.dao.hibernate.UserHibernateDAO;

import edu.yale.library.beans.*;

public class DaoHibernateModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        TypeLiteral<GenericDAO<User, Integer>> userDaoType
                = new TypeLiteral<GenericDAO<User, Integer>>() {;};

        TypeLiteral<GenericDAO<Project, Integer>> projectDaoType
                = new TypeLiteral<GenericDAO<Project, Integer>>() {;};

        bind(userDaoType).to(UserDAO.class);
        //bind(projectDaoType).to(ProjectDAO.class);

        bind(UserDAO.class).to(UserHibernateDAO.class);  //N.B. a.l. stub required.
    }
}
