
package com.project.account_service.socket;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.account_service.service.BankService;
import com.project.account_service.socket.parser.AccountCreation;
import com.project.account_service.socket.parser.BlockAccount;
import com.project.account_service.socket.parser.TransferMoney;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("tcp_server")
public class TcpSocket {

    static final int PACKET_SIZE = 8;

    @Autowired
    private  BankService bankService;

    public void listen() {

        try (
                ServerSocket soc = new ServerSocket(9000)) {

            while (true) {

                Socket client = soc.accept();
                log.info("Client connected on : {}:{} ", client.getInetAddress(), client.getPort());
                new Thread(() -> handleClient(client)).start();
                ;
            }

        } catch (Exception e) {
            log.info(e.getMessage());

        }

    }

    public void handleClient(Socket client){

        try (
                client;
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();) {

            byte headerBytes[] = new byte[PACKET_SIZE];

            while (true) {

                int total = 0;

                while (total < PACKET_SIZE) {

                    int n = in.read(headerBytes, total, PACKET_SIZE - total);

                    if (n == -1)
                        return;

                    total += n;

                }

                ByteBuffer buffer = ByteBuffer.wrap(headerBytes);

                int len = buffer.getInt();
                int type = buffer.getInt();

                // log.info("jsong len {}", jsonLen);

                byte payload[] = new byte[len];

                total = 0;

                while (total < len) {

                    int bytesRead = in.read(payload,total,len - total);

                    if (bytesRead == -1)
                        throw new EOFException("End of file");

                    total += bytesRead;



                }

                switch (type) {
                    case 1 -> {
                        String accountNumber = parseAccountCreation(payload) ;
                        sendAccountCreationResponse(out, accountNumber);

                    }
                    case 2 -> parseTransferMoney(payload);
                    case 3 -> parseDeleteAccount(payload);
                    default -> log.info("Default log");
                }

                log.info("type {}", type);
                log.info("length {}", len);
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    private String parseAccountCreation(byte[] payload) {
        try {
        ByteBuffer buffer = ByteBuffer.wrap(payload);

        AccountCreation accountCreation = new AccountCreation(buffer.getInt());

        return bankService.accountCreation(accountCreation);
            
        } catch (Exception e) {
            log.info("error: {}",e.getMessage());
            return null;
        }


    }

    private void parseDeleteAccount(byte[] payload) {

        ByteBuffer buffer = ByteBuffer.wrap(payload);

        BlockAccount vBlockAccount = new BlockAccount(buffer.getInt());

        bankService.deleteAccount(vBlockAccount.getAccountId());

    }
    private void parseTransferMoney(byte[] payload) {

        ByteBuffer buffer = ByteBuffer.wrap(payload);

        byte senderBytes[] = new byte[37];
        buffer.get(senderBytes);

        byte receiverBytes[] = new byte[37];
        buffer.get(receiverBytes);

        int amount = buffer.getInt();

        String receiver = new String(receiverBytes).trim();
        String sender = new String(senderBytes).trim();

        TransferMoney transferMoney =  new TransferMoney(
            sender,receiver,amount
    );

    bankService.transferMoney(transferMoney);

    }

    private void sendAccountCreationResponse(
        OutputStream out,
        String accountNumber) throws Exception {

    byte[] accountBytes =
            accountNumber.getBytes(StandardCharsets.UTF_8);

    ByteBuffer response =
            ByteBuffer.allocate(8 + accountBytes.length);

    response.putInt(accountBytes.length); // payload length
    response.putInt(101);                 // response packet type
    response.put(accountBytes);

    out.write(response.array());
    out.flush();
}
}
