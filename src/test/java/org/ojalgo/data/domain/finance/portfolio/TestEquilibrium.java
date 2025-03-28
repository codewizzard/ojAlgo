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
package org.ojalgo.data.domain.finance.portfolio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.ojalgo.TestUtils;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.constant.BigMath;
import org.ojalgo.function.constant.PrimitiveMath;
import org.ojalgo.matrix.MatrixR064;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.R064Store;
import org.ojalgo.random.Uniform;
import org.ojalgo.type.StandardType;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

public class TestEquilibrium extends FinancePortfolioTests {

    public static void main(final String[] args) {
        int assetNum = 16;

        double[][] om = { { 0.003330616, 0.003524811, 0.00386567, 0.003656347, 0.004494241, 0.004623772, 0.00458625, 0.004365933, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0.003524811, 0.004274864, 0.004372518, 0.004135748, 0.005144421, 0.005292691, 0.005249742, 0.004997551, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0.00386567, 0.004372518, 0.005114057, 0.004535687, 0.005641369, 0.005803962, 0.005756863, 0.005480312, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0.003656347, 0.004135748, 0.004535687, 0.004728464, 0.005511769, 0.005670626, 0.00562461, 0.005354411, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0.004494241, 0.005144421, 0.005641369, 0.005511769, 0.007284319, 0.006518612, 0.006359324, 0.00635862, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0.004623772, 0.005292691, 0.005803962, 0.005670626, 0.006518612, 0.007542516, 0.006562129, 0.006561403, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0.00458625, 0.005249742, 0.005756863, 0.00562461, 0.006359324, 0.006562129, 0.007513433, 0.00640107, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0.004365933, 0.004997551, 0.005480312, 0.005354411, 0.00635862, 0.006561403, 0.00640107, 0.006805889, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0.000152891, 0.000120753, 7.98783E-05, 0.000116607, 8.11225E-05, 4.31112E-05, 6.11874E-05, 1.49376E-07 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0.000120753, 0.000215819, 0.000108447, 0.000158311, 7.62318E-05, 4.05121E-05, 5.74986E-05, 1.4037E-07 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 7.98783E-05, 0.000108447, 0.000251455, 0.000104723, 0.000105337, 5.59796E-05, 7.94514E-05, 1.93963E-07 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0.000116607, 0.000158311, 0.000104723, 0.000285454, 0.000109622, 5.82568E-05, 8.26835E-05, 2.01854E-07 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 8.11225E-05, 7.62318E-05, 0.000105337, 0.000109622, 0.000110962, 5.62808E-05, 7.47961E-05, 1.97502E-06 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 4.31112E-05, 4.05121E-05, 5.59796E-05, 5.82568E-05, 5.62808E-05, 4.12145E-05, 4.25652E-05, 1.12395E-06 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 6.11874E-05, 5.74986E-05, 7.94514E-05, 8.26835E-05, 7.47961E-05, 4.25652E-05, 7.62351E-05, 1.4937E-06 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1.49376E-07, 1.4037E-07, 1.93963E-07, 2.01854E-07, 1.97502E-06, 1.12395E-06, 1.4937E-06, 6.52443E-06 } };

        TestEquilibrium tm = new TestEquilibrium();

        MatrixR064 covariances = tm.getCovariances(om);

        System.out.println(covariances);

        BigDecimal riskAversion = new BigDecimal(1000.0);

        MarketEquilibrium marketEquilibrium = new MarketEquilibrium(covariances, riskAversion);

        MatrixR064.DenseReceiver expectedExcessReturns1 = MatrixR064.FACTORY.newDenseBuilder(assetNum, 1);
        expectedExcessReturns1.set(0, 0, 0.03360872);
        expectedExcessReturns1.set(1, 0, 0.027322319);
        expectedExcessReturns1.set(2, 0, 0.027668137);
        expectedExcessReturns1.set(3, 0, 0.03080239);
        expectedExcessReturns1.set(4, 0, 0.025067124);
        expectedExcessReturns1.set(5, 0, 0.016578507);
        expectedExcessReturns1.set(6, 0, 0.022622714);
        expectedExcessReturns1.set(7, 0, 0.028957183);
        expectedExcessReturns1.set(8, 0, 0.009939058);
        expectedExcessReturns1.set(9, 0, 0.010014445);
        expectedExcessReturns1.set(10, 0, 0.011565874);
        expectedExcessReturns1.set(11, 0, 0.011609169);
        expectedExcessReturns1.set(12, 0, 0.006286505);
        expectedExcessReturns1.set(13, 0, 0.004240681);
        expectedExcessReturns1.set(14, 0, 0.006162067);
        expectedExcessReturns1.set(15, 0, 0.003081388);

        System.out.println("Return Matrix" + expectedExcessReturns1.get());

        MarkowitzModel markowitzModel = new MarkowitzModel(marketEquilibrium, expectedExcessReturns1.get());

        //markowitzModel.setTargetReturn(new BigDecimal("0.01051787"));
        markowitzModel.setTargetReturn(new BigDecimal("0.003081388"));

        for (int i = 0; i < assetNum; i++) {
            markowitzModel.setLowerLimit(i, new BigDecimal(0.0));
            markowitzModel.setUpperLimit(i, new BigDecimal(1.0));
        }

        List<BigDecimal> re = markowitzModel.getWeights();

        System.out.println("=======result====================");
        for (int i = 0; i < re.size(); i++) {
            System.out.println(re.get(i));
        }

        System.out.println("=======result====================");
        System.out.println(markowitzModel.getMeanReturn());
        System.out.println(markowitzModel.getReturnVariance());
        return;
    }

    public TestEquilibrium() {
        super();
    }

    public MatrixR064 getCovariances(final double[][] returns) {

        int row = returns.length;
        int col = returns[0].length;

        MatrixR064.DenseReceiver covariances = MatrixR064.FACTORY.newDenseBuilder(row, col);

        for (int i = 1; i <= row; i++) {
            for (int j = i; j <= col; j++) {
                covariances.set(i - 1, j - 1, returns[i - 1][j - 1]);
                covariances.set(j - 1, i - 1, returns[j - 1][i - 1]);
            }

        }
        return covariances.get();
    }

    @Test
    @Tag("unstable")
    public void testRandomProblemsComparedToEquilibrium() {

        NumberContext weightsContext = StandardType.PERCENT.withPrecision(5);

        int dim = 9;

        Uniform uniformCorrelation = new Uniform(-0.5, 1.0);
        Uniform uniformVolatility = new Uniform(0.01, 0.10);
        Uniform uniformRiskAversionExponent = new Uniform(-1.0, 3.0);
        Uniform uniformWeight = new Uniform(0.0, 1.0);

        PhysicalStore<Double> covarianceMatrix = R064Store.FACTORY.makeFilled(dim, dim, uniformCorrelation);
        covarianceMatrix.fillDiagonal(1.0);
        covarianceMatrix.modifyAll(PrimitiveMath.DIVIDE.by(2.0));
        covarianceMatrix.modifyMatching(PrimitiveMath.ADD, covarianceMatrix.transpose());
        for (int ij = 0; ij < dim; ij++) {
            UnaryFunction<Double> modifier = PrimitiveMath.MULTIPLY.by(uniformVolatility.doubleValue());
            covarianceMatrix.modifyRow(ij, modifier);
            covarianceMatrix.modifyColumn(ij, modifier);
        }

        double raf = PrimitiveMath.POW.invoke(10.0, uniformRiskAversionExponent.doubleValue());

        MarketEquilibrium equilibrium = new MarketEquilibrium(covarianceMatrix, raf).clean();

        double[] rawWeights = MatrixR064.FACTORY.makeFilled(dim, 1, uniformWeight).toRawCopy1D();
        List<BigDecimal> normalisedWeights = new SimplePortfolio(rawWeights).normalise().getWeights();

        MatrixR064 generatedWeights = MatrixR064.FACTORY.column(normalisedWeights);
        MatrixR064 matchingReturns = equilibrium.calculateAssetReturns(generatedWeights);
        TestUtils.assertEquals(generatedWeights, equilibrium.calculateAssetWeights(matchingReturns), weightsContext);

        FixedWeightsPortfolio portfFW = new FixedWeightsPortfolio(equilibrium, generatedWeights);
        TestUtils.assertEquals(matchingReturns, portfFW.getAssetReturns(), weightsContext);

        FixedReturnsPortfolio portfFR = new FixedReturnsPortfolio(equilibrium, matchingReturns);
        TestUtils.assertEquals(generatedWeights, portfFR.getAssetWeights(), weightsContext);

        BlackLittermanModel modelBL = new BlackLittermanModel(equilibrium, generatedWeights);
        for (int i = 0; i < dim; i++) {
            List<BigDecimal> viewAssetWeights = new ArrayList<>();
            for (int j = 0; j < dim; j++) {
                if (i == j) {
                    viewAssetWeights.add(BigMath.ONE);
                } else {
                    viewAssetWeights.add(BigMath.ZERO);
                }
            }
            int row = i;
            BigDecimal viewReturn = TypeUtils.toBigDecimal(matchingReturns.get(row, 0));
            modelBL.addViewWithScaledConfidence(viewAssetWeights, viewReturn, BigMath.ONE);
        }
        TestUtils.assertEquals(generatedWeights, modelBL.getAssetWeights(), weightsContext);

        MarkowitzModel modelM = new MarkowitzModel(equilibrium, matchingReturns);
        TestUtils.assertEquals(generatedWeights, modelM.getAssetWeights(), weightsContext);
    }

}
