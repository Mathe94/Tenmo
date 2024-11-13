package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDTO;

import java.util.List;

public interface TransferDao {
    TransferDTO createTransfer(TransferDTO transfer);
    List<TransferDTO> getTransfersByUserId(int userId);
    TransferDTO getTransferById(int transferId);

    //int getAccountIdByUserId(int userId);
}
