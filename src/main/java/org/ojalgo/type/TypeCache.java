/*
 * Copyright 1997-2025 Optimatika
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.type;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A supplier that most of the time returns a cached value, and only recomputes it when necessary
 * (periodically).
 *
 * @deprecated v56 Use {@link ForgetfulMap#newValueCache(Object, java.util.function.Function)} instead.
 */
@Deprecated
public abstract class TypeCache<T> implements ForgetfulMap.ValueCache<T> {

    private static final Timer TIMER = new Timer("TypeCache-Daemon", true);
    private transient volatile T myCachedObject;
    private volatile boolean myDirty;

    public TypeCache(final long aPurgeIntervalMeassure, final CalendarDateUnit aPurgeIntervalUnit) {

        super();

        TIMER.schedule(new TimerTask() {

            @Override
            public void run() {
                if (TypeCache.this.isDirty()) {
                    TypeCache.this.flushCache();
                } else {
                    TypeCache.this.makeDirty();
                }
            }

        }, 0L, aPurgeIntervalMeassure * aPurgeIntervalUnit.toDurationInMillis());
    }

    @SuppressWarnings("unused")
    private TypeCache() {
        this(8L, CalendarDateUnit.HOUR);
    }

    @Override
    public synchronized final void flushCache() {
        myCachedObject = null;
    }

    @Override
    public synchronized final T getCachedObject() {

        if (myCachedObject == null || myDirty) {

            myCachedObject = this.recreateCache();

            myDirty = false;
        }

        return myCachedObject;
    }

    @Override
    public synchronized final boolean isCacheSet() {
        return myCachedObject != null;
    }

    @Override
    public synchronized final boolean isDirty() {
        return myDirty;
    }

    @Override
    public synchronized final void makeDirty() {
        myDirty = true;
    }

    protected abstract T recreateCache();

}
