package com.revolut.transfersvc.service;

import com.revolut.transfersvc.jersey.exceptions.EntityHasIdentifierException;
import com.revolut.transfersvc.jersey.exceptions.EntityNotFoundException;
import com.revolut.transfersvc.jersey.exceptions.NoEntityIdentifierException;
import com.revolut.transfersvc.model.BankAccount;
import com.revolut.transfersvc.persistence.hk2.Transactional;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import java.sql.SQLIntegrityConstraintViolationException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/bankAccounts")
@Intercepted
public class BankAccountService {

    @Inject
    private EntityManager em;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Transactional
    public BankAccount create(BankAccount bankAccount) {
        if (bankAccount.getId() != null) {
            throw new EntityHasIdentifierException();
        }
        try {
            em.persist(bankAccount);
            em.flush();
        } catch (PersistenceException pe) {
            if (pe.getCause() instanceof DatabaseException) {
                DatabaseException de = (DatabaseException) pe.getCause();
                if (de.getCause() instanceof SQLIntegrityConstraintViolationException) {
                    throw new WebApplicationException("Such bank account number is already exists", BAD_REQUEST);
                }
            }
            throw pe;
        }
        return bankAccount;
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Transactional
    public BankAccount read(@PathParam("id") long id) {
        final BankAccount bankAccount = em.find(BankAccount.class, id);
        if (bankAccount == null) {
            throw new EntityNotFoundException();
        }
        return bankAccount;
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Transactional
    public BankAccount update(BankAccount bankAccount) {
        if (bankAccount.getId() == null) {
            throw new NoEntityIdentifierException();
        }
        em.merge(bankAccount);
        return bankAccount;
    }

}

