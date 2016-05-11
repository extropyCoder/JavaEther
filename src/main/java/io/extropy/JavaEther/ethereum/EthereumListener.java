package io.extropy.JavaEther.ethereum;

import org.ethereum.core.Block;
import org.ethereum.core.Transaction;
import org.ethereum.core.TransactionReceipt;
import org.ethereum.crypto.ECKey;
import org.ethereum.facade.Ethereum;
import org.ethereum.listener.EthereumListenerAdapter;
import org.ethereum.util.BIUtil;
import org.ethereum.util.ByteUtil;
import org.slf4j.Logger;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.util.List;

public class EthereumListener extends EthereumListenerAdapter {

    Ethereum ethereum;
    private boolean syncDone = false;

    public EthereumListener(Ethereum ethereum) {
        this.ethereum = ethereum;
    }

    @Override
    public void onBlock(Block block, List<TransactionReceipt> receipts) {
        System.out.println();
        System.out.println("Do something on block: " + block.getNumber());

        if (syncDone)
            calcNetHashRate(block);
//        try {
//            generateTransactions();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println();
    }



    /**
     *  Mark the fact that you are touching
     *  the head of the chain
     */
    @Override
    public void onSyncDone() {

        System.out.println(" ** SYNC DONE ** ");
        syncDone = true;
    }


    private void generateTransactions() throws Exception{
        System.out.println("Start generating transactions...");

        // the sender which some coins from the genesis
        ECKey senderKey = ECKey.fromPrivate(Hex.decode("6ef8da380c27cea8fdf7448340ea99e8e2268fc2950d79ed47cbf6f85dc977ec"));
        byte[] receiverAddr = Hex.decode("5db10750e8caff27f906b41c71b3471057dd2004");

        for (int i = ethereum.getRepository().getNonce(senderKey.getAddress()).intValue(), j = 0; j < 20; i++, j++) {
            {
                Transaction tx = new Transaction(ByteUtil.intToBytesNoLeadZeroes(i),
                        ByteUtil.longToBytesNoLeadZeroes(50_000_000_000L), ByteUtil.longToBytesNoLeadZeroes(0xfffff),
                        receiverAddr, new byte[]{77}, new byte[0]);
                tx.sign(senderKey.getPrivKeyBytes());
                System.out.println("<== Submitting tx: " + tx);
                ethereum.submitTransaction(tx);
            }
            Thread.sleep(7000);
        }
    }



    /**
     * Just small method to estimate total power off all miners on the net
     * @param block
     */
    private void calcNetHashRate(Block block){

        if ( block.getNumber() > 1000){

            long avgTime = 1;
            long cumTimeDiff = 0;
            Block currBlock = block;
            for (int i=0; i < 1000; ++i){

                Block parent = ethereum.getBlockchain().getBlockByHash(currBlock.getParentHash());
                long diff = currBlock.getTimestamp() - parent.getTimestamp();
                cumTimeDiff += Math.abs(diff);
                currBlock = parent;
            }

            avgTime = cumTimeDiff / 1000;

            BigInteger netHashRate = block.getDifficultyBI().divide(BIUtil.toBI(avgTime));
            double hashRate = netHashRate.divide(new BigInteger("1000000000")).doubleValue();

            System.out.println("Net hash rate: " + hashRate + " GH/s");
        }

    }

}
