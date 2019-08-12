package com.revolut.transfersvc.service;

import com.revolut.transfersvc.jersey.exceptions.EntityHasIdentifierException;
import com.revolut.transfersvc.jersey.exceptions.EntityNotFoundException;
import com.revolut.transfersvc.jersey.exceptions.WebApplicationException;
import com.revolut.transfersvc.model.BankAccount;
import com.revolut.transfersvc.model.Payment;
import com.revolut.transfersvc.persistence.hk2.Transactional;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import java.util.Date;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/payments")
@Intercepted
public class TransferService {

    @Inject
    private EntityManager em;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Transactional
    public Payment create(Payment payment) {
        if (payment.getId() != null) {
            throw new EntityHasIdentifierException();
        }
        if (payment.getFunds() == null) {
            throw new WebApplicationException("Payment funds is undefined", BAD_REQUEST);
        }
        payment.setDate(new Date());

        // set timeout to obtain pessimistic lock over the bank accounts
        em.setProperty("javax.persistence.lock.timeout", 1000L);

        final BankAccount src;
        final BankAccount dst;
        if (payment.getWithdrawalBankAccountNumber() != payment.getDepositBankAccountNumber()) {
            src = getWithdrawalBankAccount(payment);
            dst = getBankAccount(payment.getDepositBankAccountNumber(), "deposit");
        } else {
            throw new WebApplicationException("Withdrawal and deposit bank accounts are the same", BAD_REQUEST);
        }


        src.setBalance(src.getBalance().subtract(payment.getFunds()));
        dst.setBalance(dst.getBalance().add(payment.getFunds()));

        em.merge(src);
        em.merge(dst);
        em.persist(payment);

        return payment;
    }

    private BankAccount getWithdrawalBankAccount(Payment payment) {
        final BankAccount bankAccount = getBankAccount(payment.getWithdrawalBankAccountNumber(), "withdrawal");
        if (bankAccount.getBalance().compareTo(payment.getFunds()) < 0) {
            throw new WebApplicationException("Withdrawal bank account has insufficient balance for the payment", BAD_REQUEST);
        }
        return bankAccount;
    }

    private BankAccount getBankAccount(long number, String type) {
        try {
            return em.createNamedQuery("BankAccount.findByNumber", BankAccount.class)
                    .setParameter("number", number)
                    .setLockMode(PESSIMISTIC_WRITE)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new WebApplicationException("No " + type + "BankAccount found", BAD_REQUEST);
        }
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    @Transactional
    public Payment read(@PathParam("id") long id) {
        final Payment payment = em.find(Payment.class, id);
        if (payment == null) {
            throw new EntityNotFoundException();
        }
        return payment;
    }

}
