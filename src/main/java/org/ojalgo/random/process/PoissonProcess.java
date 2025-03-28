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
package org.ojalgo.random.process;

import static org.ojalgo.function.constant.PrimitiveMath.SQRT;
import static org.ojalgo.function.constant.PrimitiveMath.ZERO;

import org.ojalgo.ProgrammingError;
import org.ojalgo.random.Exponential;
import org.ojalgo.random.Poisson;

/**
 * A Poisson process is a stochastic process which counts the number of events in a given time interval. The
 * time between each pair of consecutive events has an exponential distribution with parameter λ and each of
 * these inter-arrival times is assumed to be independent of other inter-arrival times. The process is a good
 * model of radioactive decay, telephone calls and requests for a particular document on a web server, among
 * many other phenomena.
 *
 * @author apete
 */
public final class PoissonProcess extends SingleValueBasedProcess<Poisson> {

    private static final Poisson GENERATOR = new Poisson();

    private final double myRate; // lambda, intensity

    protected PoissonProcess(final double rate) {

        super();

        this.setCurrentValue(ZERO);

        myRate = rate;
    }

    public Poisson getDistribution(final double evaluationPoint) {
        return new Poisson(myRate * evaluationPoint);
    }

    public Exponential getTimeBetweenConsecutiveEvents() {
        return new Exponential(myRate);
    }

    @Override
    double doStep(final double stepSize, final double normalisedRandomIncrement) {
        double retVal = this.getCurrentValue() + ((myRate * stepSize) * normalisedRandomIncrement);
        this.setCurrentValue(retVal);
        return retVal;
    }

    @Override
    double getExpected(final double stepSize) {
        return myRate * stepSize;
    }

    @Override
    double getLowerConfidenceQuantile(final double stepSize, final double confidence) {
        ProgrammingError.throwForUnsupportedOptionalOperation();
        return ZERO;
    }

    @Override
    double getNormalisedRandomIncrement() {
        return GENERATOR.doubleValue();
    }

    @Override
    double getStandardDeviation(final double stepSize) {
        return SQRT.invoke(myRate * stepSize);
    }

    @Override
    double getUpperConfidenceQuantile(final double stepSize, final double confidence) {
        ProgrammingError.throwForUnsupportedOptionalOperation();
        return ZERO;
    }

    @Override
    double getVariance(final double stepSize) {
        return myRate * stepSize;
    }

}
