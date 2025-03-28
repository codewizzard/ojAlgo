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
package org.ojalgo.random;

import static org.ojalgo.function.constant.PrimitiveMath.ONE;
import static org.ojalgo.function.constant.PrimitiveMath.ZERO;

import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.function.special.MissingMath;

/**
 * The Poisson distribution is a discrete probability distribution that expresses the probability of a given
 * number of events occurring in a fixed interval of time and/or space if these events occur with a known
 * average rate and independently of the time since the last event. (The Poisson distribution can also be used
 * for the number of events in other specified intervals such as distance, area or volume.) Distribution of
 * number of points in random point process under certain simple assumptions. Approximation to the binomial
 * distribution when aCount is large and aProbability is small. aLambda = aCount * aProbability.
 *
 * @author apete
 */
public class Poisson extends AbstractDiscrete {

    public static Poisson of(final double lambda) {
        return new Poisson(lambda);
    }

    private final double myLambda; // rate or intensity

    public Poisson() {
        this(ONE);
    }

    public Poisson(final double lambda) {

        super();

        myLambda = lambda;
    }

    public double getExpected() {
        return myLambda;
    }

    public double getProbability(final int value) {
        return (PrimitiveMath.EXP.invoke(-myLambda) * PrimitiveMath.POW.invoke(myLambda, value)) / MissingMath.factorial(value);
    }

    @Override
    public double getVariance() {
        return myLambda;
    }

    @Override
    protected double generate() {

        int retVal = -1;
        double tmpVal = ZERO;

        while (tmpVal <= ONE) {

            retVal++;

            tmpVal -= PrimitiveMath.LOG.invoke(this.random().nextDouble()) / myLambda;
        }

        return retVal;
    }

}
