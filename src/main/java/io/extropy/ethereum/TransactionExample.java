package io.extropy.ethereum;

import org.ethereum.core.Block;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.listener.EthereumListenerAdapter;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.ethereum.crypto.HashUtil.sha3;
import static org.ethereum.util.ByteUtil.longToBytesNoLeadZeroes;

public class TransactionExample extends EthereumListenerAdapter {


    Ethereum ethereum = null;
    boolean startedTxBomb = false;

    public TransactionExample(Ethereum ethereum) {
        this.ethereum = ethereum;
    }

    public static void main(String[] args) {

        Ethereum ethereum = EthereumFactory.createEthereum();
        ethereum.addListener(new TransactionExample(ethereum));
    }


    @Override
    public void onSyncDone() {
        startedTxBomb = true;
        System.err.println(" ~~~ SYNC DONE ~~~ ");
        byte[] sender = Hex.decode("cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
        long nonce = ethereum.getRepository().getNonce(sender).longValue();
        ;
        BigInteger bal = ethereum.getRepository().getBalance(sender);
        System.err.println(" balance is " + bal);

        for (int i = 0; i < 2; ++i) {
            sendTx(nonce);
            System.err.println(" nonce is " + nonce);
            ++nonce;
            sleep(10);
        }
    }

    @Override
    public void onBlock(Block block, List<TransactionReceipt> receipts) {

    }

    private void sendTx(long nonce) {

        byte[] gasPrice = longToBytesNoLeadZeroes(ethereum.getGasPrice());
        byte[] gasLimit = longToBytesNoLeadZeroes(2100);

        byte[] toAddress = Hex.decode("39fa3a0388d851fbf31eacf89b1e48daf0d11ab7");
        byte[] value = longToBytesNoLeadZeroes(10);

        Transaction tx = new Transaction(longToBytesNoLeadZeroes(nonce),
                gasPrice,
                gasLimit,
                toAddress,
                value,
                null);

        byte[] privKey = sha3("cow".getBytes());
        tx.sign(privKey);
        ethereum.getChannelManager().sendTransaction(Collections.singletonList(tx), null);
        System.err.println("Sending tx: " + Hex.toHexString(tx.getHash()));
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
