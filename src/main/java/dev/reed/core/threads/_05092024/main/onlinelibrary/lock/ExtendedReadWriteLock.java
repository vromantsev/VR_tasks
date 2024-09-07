package dev.reed.core.threads._05092024.main.onlinelibrary.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class ExtendedReadWriteLock implements ReadWriteLock {

    private final ReadWriteLock delegate;
    private boolean isReadLockUsed;
    private boolean isWriteLockUsed;

    public ExtendedReadWriteLock(final ReadWriteLock delegate) {
        this.delegate = delegate;
    }

    @Override
    public Lock readLock() {
        isReadLockUsed = true;
        return delegate.readLock();
    }

    @Override
    public Lock writeLock() {
        isWriteLockUsed = true;
        return delegate.writeLock();
    }

    public boolean isReadLockUsed() {
        return isReadLockUsed;
    }

    public boolean isWriteLockUsed() {
        return isWriteLockUsed;
    }

    public void reset() {
        this.isReadLockUsed = false;
        this.isWriteLockUsed = false;
    }
}
