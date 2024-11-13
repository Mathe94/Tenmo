package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;

    }

    @Override
    public TransferDTO createTransfer(TransferDTO transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";

        int transferId = jdbcTemplate.queryForObject(sql, Integer.class,
                transfer.getTypeId(), transfer.getStatusId(),
                transfer.getAccountFrom(), transfer.getAccountTo(),
                transfer.getTransferAmount());

        transfer.setTransferId(transferId);
        return transfer;
    }

    @Override
    public List<TransferDTO> getTransfersByUserId(int userId) {
        int accountId = userDao.getAccountIdByUserId(userId);
        String sql = "SELECT * FROM transfer WHERE account_from = ? OR account_to = ?";
        //
        return jdbcTemplate.query(sql, (rs, rowNum) -> new TransferDTO(
                rs.getInt("transfer_id"),
                rs.getInt("transfer_type_id"),
                rs.getInt("transfer_status_id"),
                rs.getInt("account_from"),
                rs.getInt("account_to"),
                rs.getDouble("amount")
        ), accountId, accountId);
    }

    @Override
    public TransferDTO getTransferById(int transferId) {
        return null;
    }


}
