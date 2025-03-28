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
package org.ojalgo.matrix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.R064Store;
import org.ojalgo.type.context.NumberContext;

/**
 * Discovered problems with calculating the LU decompositions for fat and/or tall matrices. Problems were
 * found with all three implementations (including Jama).
 *
 * @author apete
 */
public class P20071019Case extends BasicMatrixTest {

    private static final NumberContext DEFINITION = NumberContext.ofScale(9);

    public static MatrixR064 getFatProblematic() {
        return SimpleLeastSquaresCase.getBody().transpose();
    }

    public static MatrixR064 getTallProblematic() {
        return SimpleLeastSquaresCase.getBody();
    }

    @Override
    @BeforeEach
    public void doBeforeEach() {

        mtrxA = P20071019Case.getFatProblematic().multiply(P20071019Case.getTallProblematic()).enforce(DEFINITION);
        mtrxX = BasicMatrixTest.getIdentity(mtrxA.countColumns(), mtrxA.countColumns(), DEFINITION);
        mtrxB = mtrxA;

        mtrxI = BasicMatrixTest.getIdentity(mtrxA.countRows(), mtrxA.countColumns(), DEFINITION);
        mtrxSafe = BasicMatrixTest.getSafe(mtrxA.countRows(), mtrxA.countColumns(), DEFINITION);

        super.doBeforeEach();
    }

    @Test
    public void testData() {

        TestUtils.assertEquals(true, P20071019Case.getFatProblematic().isFat());

        TestUtils.assertEquals(true, P20071019Case.getTallProblematic().isTall());
    }

    @Test
    public void testProblem() {

        LU<Double> tmpJamaLU = LU.R064.make();
        LU<Double> tmpDenseLU = LU.R064.make();

        MatrixStore<Double> tmpOriginal = R064Store.FACTORY.copy(P20071019Case.getFatProblematic());

        tmpJamaLU.decompose(tmpOriginal);
        TestUtils.assertEquals(tmpOriginal, tmpJamaLU, ACCURACY);

        tmpDenseLU.decompose(tmpOriginal);
        TestUtils.assertEquals(tmpOriginal, tmpDenseLU, ACCURACY);

        tmpOriginal = R064Store.FACTORY.copy(P20071019Case.getTallProblematic());

        tmpJamaLU.decompose(tmpOriginal);
        TestUtils.assertEquals(tmpOriginal, tmpJamaLU, ACCURACY);

        tmpDenseLU.decompose(tmpOriginal);
        TestUtils.assertEquals(tmpOriginal, tmpDenseLU, ACCURACY);

    }

}
