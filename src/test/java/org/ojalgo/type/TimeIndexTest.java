/*
 * Copyright 1997-2023 Optimatika
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

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.structure.Structure1D.IndexMapper;

public class TimeIndexTest {

    private static final ZoneId SYS_DEF_ZONE = ZoneId.systemDefault();

    static <T extends Comparable<? super T>> void doTestFrom(final TimeIndex<T> mapperFactory, final T keyToTest, final T referenceKey) {

        final String implementation = keyToTest.getClass().getSimpleName();

        final IndexMapper<T> mapper = mapperFactory.from(referenceKey);

        final T expected = keyToTest;
        final long index = mapper.toIndex(expected);
        final T actual = mapper.toKey(index);

        TestUtils.assertEquals(implementation, expected, actual);
    }

    static <T extends Comparable<? super T>> void doTestFromWithResolution(final TimeIndex<T> mapperFactory, final T keyToTest, final T referenceKey) {

        final String implementation = keyToTest.getClass().getSimpleName();

        final IndexMapper<T> mapper = mapperFactory.from(referenceKey);

        final T expected = keyToTest;
        final long index = mapper.toIndex(expected);
        final T actual = mapper.toKey(index);

        TestUtils.assertEquals(implementation, expected, actual);

        for (final CalendarDateUnit resolution : CalendarDateUnit.values()) {

        }

    }

    static <T extends Comparable<? super T>> void doTestPlain(final TimeIndex<T> mapperFactory, final T keyToTest) {

        final String implementation = keyToTest.getClass().getSimpleName();

        final IndexMapper<T> mapper = mapperFactory.plain();

        final T expected = keyToTest;
        final long index = mapper.toIndex(expected);
        final T actual = mapper.toKey(index);

        TestUtils.assertEquals(implementation, expected, actual);
    }

    @Test
    public void testFrom() {

        // ms precision (strip micros and nanos)
        final Instant instant = Instant.ofEpochMilli(Instant.now().toEpochMilli());
        // Reference 10 years ago
        final Instant reference = instant.minusMillis(TypeUtils.HOURS_PER_CENTURY * TypeUtils.MILLIS_PER_HOUR / 10L);

        TimeIndexTest.doTestFrom(TimeIndex.INSTANT, instant, reference);

        TimeIndexTest.doTestFrom(TimeIndex.DATE, CalendarDate.toDate(instant), CalendarDate.toDate(reference));
        TimeIndexTest.doTestFrom(TimeIndex.CALENDAR, CalendarDate.toCalendar(instant), CalendarDate.toCalendar(reference));

        TimeIndexTest.doTestFrom(TimeIndex.CALENDAR_DATE, CalendarDate.valueOf(instant), CalendarDate.valueOf(reference));

        TimeIndexTest.doTestFrom(TimeIndex.LOCAL_DATE_TIME, CalendarDate.toLocalDateTime(instant, SYS_DEF_ZONE),
                CalendarDate.toLocalDateTime(reference, SYS_DEF_ZONE));
        TimeIndexTest.doTestFrom(TimeIndex.LOCAL_DATE, CalendarDate.toLocalDate(instant, SYS_DEF_ZONE), CalendarDate.toLocalDate(reference, SYS_DEF_ZONE));
        TimeIndexTest.doTestFrom(TimeIndex.LOCAL_TIME, CalendarDate.toLocalTime(instant, SYS_DEF_ZONE), CalendarDate.toLocalTime(reference, SYS_DEF_ZONE));

        TimeIndexTest.doTestFrom(TimeIndex.OFFSET_DATE_TIME, CalendarDate.toOffsetDateTime(instant, SYS_DEF_ZONE, reference),
                CalendarDate.toOffsetDateTime(reference, SYS_DEF_ZONE));

        TimeIndexTest.doTestFrom(TimeIndex.ZONED_DATE_TIME, CalendarDate.toZonedDateTime(instant, SYS_DEF_ZONE),
                CalendarDate.toZonedDateTime(reference, SYS_DEF_ZONE));

        final ZoneOffset refSysOffset = OffsetDateTime.ofInstant(reference, SYS_DEF_ZONE).getOffset();

        TimeIndexTest.doTestFrom(TimeIndex.ZONED_DATE_TIME, CalendarDate.toZonedDateTime(instant, refSysOffset),
                CalendarDate.toZonedDateTime(reference, refSysOffset));
    }

    @Test
    public void testPlain() {

        // ms precision (strip micros and nanos)
        final Instant instant = Instant.ofEpochMilli(Instant.now().toEpochMilli());

        TimeIndexTest.doTestPlain(TimeIndex.INSTANT, instant);

        TimeIndexTest.doTestPlain(TimeIndex.DATE, CalendarDate.toDate(instant));
        TimeIndexTest.doTestPlain(TimeIndex.CALENDAR, CalendarDate.toCalendar(instant));

        TimeIndexTest.doTestPlain(TimeIndex.CALENDAR_DATE, CalendarDate.valueOf(instant));

        TimeIndexTest.doTestPlain(TimeIndex.LOCAL_DATE_TIME, CalendarDate.toLocalDateTime(instant, SYS_DEF_ZONE));
        TimeIndexTest.doTestPlain(TimeIndex.LOCAL_DATE, CalendarDate.toLocalDate(instant, SYS_DEF_ZONE));
        TimeIndexTest.doTestPlain(TimeIndex.LOCAL_TIME, CalendarDate.toLocalTime(instant, SYS_DEF_ZONE));

        TimeIndexTest.doTestPlain(TimeIndex.OFFSET_DATE_TIME, CalendarDate.toOffsetDateTime(instant, SYS_DEF_ZONE));
        TimeIndexTest.doTestPlain(TimeIndex.ZONED_DATE_TIME, CalendarDate.toZonedDateTime(instant, SYS_DEF_ZONE));
    }

}
