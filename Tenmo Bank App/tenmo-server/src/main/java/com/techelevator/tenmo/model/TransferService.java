package com.techelevator.tenmo.model;



import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
    public class TransferService {

        private final TransferDao transferDao;
        private final UserDao userDao;

        public TransferService(TransferDao transferDao, UserDao userDao) {
            this.transferDao = transferDao;
            this.userDao = userDao;
        }


        public void validateAndProcessTransfer(TransferDTO transfer) {
            if (transfer.getAccountFrom() == transfer.getAccountTo()) {
                throw new IllegalArgumentException("Cannot send money to yourself.");
            }
            Balance senderBalance = userDao.getBalanceByUserId(transfer.getAccountFrom());

            // Ensure the transfer amount is greater than 0
            if (BigDecimal.valueOf(transfer.getTransferAmount()).compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Transfer amount must be greater than zero.");
            }
            // Ensure the sender has sufficient funds
            if (senderBalance.getAmount().compareTo(BigDecimal.valueOf(transfer.getTransferAmount())) < 0) {
                throw new IllegalArgumentException("Insufficient funds to complete this transfer.");
            }
            // Update balances: Subtract from sender, add to receiver
            userDao.updateBalance(
                    transfer.getAccountFrom(),
                    senderBalance.getAmount().subtract(BigDecimal.valueOf(transfer.getTransferAmount()))
            );
            Balance receiverBalance = userDao.getBalanceByUserId(transfer.getAccountTo());
            userDao.updateBalance(
                    transfer.getAccountTo(),
                    receiverBalance.getAmount().add(BigDecimal.valueOf(transfer.getTransferAmount()))
            );

            // Record the transfer in the database
            transferDao.createTransfer(transfer);
        }
        public List<TransferDTO> getTransfersForUser(String username) {
            int userId = userDao.getUserByUsername(username).getId();
            return transferDao.getTransfersByUserId(userId);
        }
    public TransferDTO getTransferById(int transferId) {
        return transferDao.getTransferById(transferId);
    }

}
