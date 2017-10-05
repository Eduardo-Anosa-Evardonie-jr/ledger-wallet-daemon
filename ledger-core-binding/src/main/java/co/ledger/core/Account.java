// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from wallet.djinni

package co.ledger.core;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Account {
    /**
     * Key of the synchronization duration time in the synchronize event payload.
     * The value is stored in a int 64 time expressed in miliseconds.
     */
    public static final String EV_SYNC_DURATION_MS = "EV_SYNC_DURATION_MS";

    /** Key of the synchronization error code. The code is a stringified version of the value in the ErrorCode enum. */
    public static final String EV_SYNC_ERROR_CODE = "EV_SYNC_ERROR_CODE";

    /** Key of the synchronization error message. The message is stored as a string. */
    public static final String EV_SYNC_ERROR_MESSAGE = "EV_SYNC_ERROR_MESSAGE";

    public static final String EV_NEW_BLOCK_CURRENCY_NAME = "EV_NEW_BLOCK_CURRENCY_NAME";

    public static final String EV_NEW_BLOCK_HASH = "EV_NEW_BLOCK_HASH";

    public static final String EV_NEW_BLOCK_HEIGHT = "EV_NEW_BLOCK_HEIGHT";

    public static final String EV_NEW_OP_WALLET_NAME = "EV_NEW_OP_WALLET_NAME";

    public static final String EV_NEW_OP_ACCOUNT_INDEX = "EV_NEW_OP_ACCOUNT_INDEX";

    public static final String EV_NEW_OP_UID = "EV_NEW_OP_UID";

    public abstract int getIndex();

    public abstract OperationQuery queryOperations();

    public abstract void getBalance(AmountCallback callback);

    public abstract boolean isSynchronizing();

    public abstract EventBus synchronize();

    public abstract Preferences getPreferences();

    public abstract Logger getLogger();

    public abstract Preferences getOperationPreferences(String uid);

    /**
     * asBitcoinLikeAccount(): Callback<BitcoinLikeAccount>;
     * asEthereumLikeAccount(): Callback<EthereumLikeAccount>;
     * asRippleLikeAccount(): Callback<RippleLikeAccount>;
     */
    public abstract boolean isInstanceOfBitcoinLikeAccount();

    public abstract boolean isInstanceOfEthereumLikeAccount();

    public abstract boolean isInstanceOfRippleLikeAccount();

    public abstract void getFreshPublicAddresses(StringListCallback callback);

    public abstract WalletType getWalletType();

    public abstract EventBus getEventBus();

    public abstract void computeFees(Amount amount, int priority, ArrayList<String> recipients, ArrayList<byte[]> data, AmountCallback callback);

    private static final class CppProxy extends Account
    {
        private final long nativeRef;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);

        private CppProxy(long nativeRef)
        {
            if (nativeRef == 0) throw new RuntimeException("nativeRef is zero");
            this.nativeRef = nativeRef;
        }

        private native void nativeDestroy(long nativeRef);
        public void destroy()
        {
            boolean destroyed = this.destroyed.getAndSet(true);
            if (!destroyed) nativeDestroy(this.nativeRef);
        }
        protected void finalize() throws java.lang.Throwable
        {
            destroy();
            super.finalize();
        }

        @Override
        public int getIndex()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getIndex(this.nativeRef);
        }
        private native int native_getIndex(long _nativeRef);

        @Override
        public OperationQuery queryOperations()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_queryOperations(this.nativeRef);
        }
        private native OperationQuery native_queryOperations(long _nativeRef);

        @Override
        public void getBalance(AmountCallback callback)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            native_getBalance(this.nativeRef, callback);
        }
        private native void native_getBalance(long _nativeRef, AmountCallback callback);

        @Override
        public boolean isSynchronizing()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_isSynchronizing(this.nativeRef);
        }
        private native boolean native_isSynchronizing(long _nativeRef);

        @Override
        public EventBus synchronize()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_synchronize(this.nativeRef);
        }
        private native EventBus native_synchronize(long _nativeRef);

        @Override
        public Preferences getPreferences()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getPreferences(this.nativeRef);
        }
        private native Preferences native_getPreferences(long _nativeRef);

        @Override
        public Logger getLogger()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getLogger(this.nativeRef);
        }
        private native Logger native_getLogger(long _nativeRef);

        @Override
        public Preferences getOperationPreferences(String uid)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getOperationPreferences(this.nativeRef, uid);
        }
        private native Preferences native_getOperationPreferences(long _nativeRef, String uid);

        @Override
        public boolean isInstanceOfBitcoinLikeAccount()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_isInstanceOfBitcoinLikeAccount(this.nativeRef);
        }
        private native boolean native_isInstanceOfBitcoinLikeAccount(long _nativeRef);

        @Override
        public boolean isInstanceOfEthereumLikeAccount()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_isInstanceOfEthereumLikeAccount(this.nativeRef);
        }
        private native boolean native_isInstanceOfEthereumLikeAccount(long _nativeRef);

        @Override
        public boolean isInstanceOfRippleLikeAccount()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_isInstanceOfRippleLikeAccount(this.nativeRef);
        }
        private native boolean native_isInstanceOfRippleLikeAccount(long _nativeRef);

        @Override
        public void getFreshPublicAddresses(StringListCallback callback)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            native_getFreshPublicAddresses(this.nativeRef, callback);
        }
        private native void native_getFreshPublicAddresses(long _nativeRef, StringListCallback callback);

        @Override
        public WalletType getWalletType()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getWalletType(this.nativeRef);
        }
        private native WalletType native_getWalletType(long _nativeRef);

        @Override
        public EventBus getEventBus()
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_getEventBus(this.nativeRef);
        }
        private native EventBus native_getEventBus(long _nativeRef);

        @Override
        public void computeFees(Amount amount, int priority, ArrayList<String> recipients, ArrayList<byte[]> data, AmountCallback callback)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            native_computeFees(this.nativeRef, amount, priority, recipients, data, callback);
        }
        private native void native_computeFees(long _nativeRef, Amount amount, int priority, ArrayList<String> recipients, ArrayList<byte[]> data, AmountCallback callback);
    }
}
