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
package org.ojalgo.optimisation.linear;

import static org.ojalgo.function.constant.PrimitiveMath.*;

import org.ojalgo.array.SparseArray;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.store.ColumnsSupplier;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.R064Store;
import org.ojalgo.optimisation.linear.SimplexSolver.EnterInfo;
import org.ojalgo.optimisation.linear.SimplexSolver.ExitInfo;
import org.ojalgo.optimisation.linear.SimplexSolver.IterDescr;
import org.ojalgo.structure.Mutate1D;
import org.ojalgo.structure.Mutate2D;
import org.ojalgo.structure.Primitive1D;

final class RevisedStore extends SimplexStore {

    private static R064Store newColumn(final int nbRows) {
        return R064Store.FACTORY.make(nbRows, 1);
    }

    private static ColumnsSupplier<Double> newMatrix(final int nbRows, final int nbCols) {
        ColumnsSupplier<Double> retVal = R064Store.FACTORY.makeColumnsSupplier(nbRows);
        retVal.addColumns(nbCols);
        return retVal;
    }

    private static R064Store newRow(final int nbCols) {
        return R064Store.FACTORY.make(1, nbCols);
    }

    /**
     * a(N) in Gurobi presentation - delta – reduced costs
     */
    private final PhysicalStore<Double> a;
    /**
     * Reduced costs / dual slack
     */
    private final R064Store d;
    private final R064Store l;
    private final MatrixStore<Double> myBasis;
    private final ColumnsSupplier<Double> myConstraintsBody;
    private final ColumnsSupplier.SingleView<Double> myConstraintsColumn;
    private final R064Store myConstraintsRHS;
    private final ProductFormInverse myInvBasis;
    private final R064Store myObjective;
    private R064Store myPhase1Objective = null;
    /**
     * cost reducer
     */
    private final R064Store r;
    /**
     * primal basic solution
     */
    private final R064Store x;
    /**
     * delta – primal basic solution
     */
    private final R064Store y;
    private final R064Store z;

    RevisedStore(final int mm, final int nn) {
        this(new LinearStructure(mm, nn));
    }

    RevisedStore(final LinearStructure linearStructure) {

        super(linearStructure);

        myObjective = RevisedStore.newColumn(n);
        myConstraintsBody = RevisedStore.newMatrix(m, n);
        myConstraintsRHS = RevisedStore.newColumn(m);

        myConstraintsColumn = myConstraintsBody.columns();

        x = RevisedStore.newColumn(m);
        y = RevisedStore.newColumn(m);
        z = RevisedStore.newColumn(m);
        l = RevisedStore.newColumn(m);

        d = RevisedStore.newColumn(n - m);
        a = RevisedStore.newColumn(n - m);
        r = RevisedStore.newColumn(n - m);

        myBasis = myConstraintsBody.columns(included);
        myInvBasis = new ProductFormInverse(m, TWO_THIRDS); // TODO The scaling threshold should be much smaller
    }

    private void doBodyRow(final int i, final PhysicalStore<Double> destination) {

        z.reset();
        z.set(i, ONE);
        myInvBasis.btran(z); // i:th row of inv B

        this.doExclTranspMult(z, destination);
    }

    private void doExclTranspMult(final MatrixStore<Double> lambda, final PhysicalStore<Double> results) {
        for (int je = 0; je < excluded.length; je++) {
            myConstraintsColumn.goToColumn(excluded[je]);
            results.set(je, myConstraintsColumn.dot(lambda));
        }
    }

    @Override
    protected void pivot(final IterDescr iteration) {

        int iterExitInd = iteration.exit.index;
        SparseArray<Double> iterEnterCol = myConstraintsBody.getColumn(iteration.enter.column());

        super.pivot(iteration);

        myInvBasis.update(myBasis, iterExitInd, iterEnterCol);
    }

    @Override
    protected void shiftColumn(final int col, final double shift) {
        super.shiftColumn(col, shift);
        myConstraintsBody.column(col).axpy(-shift, myConstraintsRHS);
        myInvBasis.ftran(myConstraintsRHS, x);
    }

    @Override
    void calculateDualDirection(final ExitInfo exit) {
        this.doBodyRow(exit.index, a);
    }

    @Override
    void calculateIteration() {

        //        PhysicalStore<Double> x0 = x.copy();
        //        PhysicalStore<Double> d0 = d.copy();

        // Should be able to do something like
        // x += y
        // d += a
        // ...and then move the setup/prepare/update outside of the loop

        R064Store objective = myPhase1Objective != null ? myPhase1Objective : myObjective;

        myInvBasis.btran(objective.rows(included), l);

        this.doExclTranspMult(l, r);

        d.fillMatching(objective.rows(excluded), SUBTRACT, r);

        myInvBasis.ftran(myConstraintsRHS, x);

        //        BasicLogger.debug();
        //
        //        BasicLogger.debug();
        //        BasicLogger.debug("x before: {}", x0.asList());
        //        BasicLogger.debug("Ratio primal: {} exit={}", iteration.ratioPrimal, iteration.exit.index);
        //        BasicLogger.debug("y step: {}", y.asList());
        //        BasicLogger.debug("z step: {}", z.asList());
        //        BasicLogger.debug("l step: {}", l.asList());
        //        BasicLogger.debug("x after: {}", x.asList());
        //
        //        BasicLogger.debug();
        //        BasicLogger.debug("d before: {}", d0.asList());
        //        BasicLogger.debug("Ratio dual: {} enter={}", iteration.ratioDual, iteration.enter.index);
        //        BasicLogger.debug("a step: {}", a.asList());
        //        BasicLogger.debug("r step: {}", r.asList());
        //        BasicLogger.debug("d after: {}", d.asList());
    }

    @Override
    void calculatePrimalDirection(final EnterInfo enter) {
        myConstraintsColumn.goToColumn(enter.column());
        myInvBasis.ftran(myConstraintsColumn, y);
    }

    @Override
    Mutate2D constraintsBody() {
        return myConstraintsBody;
    }

    @Override
    Mutate1D constraintsRHS() {
        return myConstraintsRHS;
    }

    @Override
    void copyBasicSolution(final double[] solution) {
        for (int ji = 0; ji < included.length; ji++) {
            int j = included[ji];
            solution[j] = x.doubleValue(ji);
        }
    }

    @Override
    double extractValue() {

        double retVal = ZERO;

        double[] solution = this.extractSolution();

        for (int i = 0; i < solution.length; i++) {
            retVal += solution[i] * myObjective.doubleValue(i);
        }

        return retVal;
    }

    @Override
    double getCost(final int j) {
        return myObjective.doubleValue(j);
    }

    @Override
    double getCurrentElement(final ExitInfo exit, final int je) {
        return a.doubleValue(je);
    }

    @Override
    double getCurrentElement(final int i, final EnterInfo enter) {
        return y.doubleValue(i);
    }

    @Override
    double getCurrentRHS(final int i) {
        return x.doubleValue(i);
    }

    @Override
    double getInfeasibility(final int i) {

        int ii = included[i];

        double xi = x.doubleValue(i);
        double lb = this.getLowerBound(ii);
        double ub = this.getUpperBound(ii);

        // BasicLogger.debug(1, "{}({}): {} < {} < {}", ii, i, lb, xi, ub);

        if (xi < lb) {
            return xi - lb; // Negative, lower bound infeasibility
        } else if (xi > ub) {
            return xi - ub; // Positive, upper bound infeasibility
        } else {
            return ZERO; // No infeasibility
        }
    }

    @Override
    double getReducedCost(final int je) {
        return d.doubleValue(je);
    }

    @Override
    Mutate1D objective() {
        return myObjective;
    }

    @Override
    R064Store phase1() {

        if (myPhase1Objective == null) {
            myPhase1Objective = RevisedStore.newColumn(n);
        }

        return myPhase1Objective;
    }

    @Override
    void removePhase1() {
        myPhase1Objective = null;
    }

    @Override
    void resetBasis(final int[] basis) {

        super.resetBasis(basis);

        myInvBasis.reset(myBasis);
    }

    @Override
    void setupClassicPhase1Objective() {

        int base = structure.nbIdty;

        if (myPhase1Objective == null) {
            myPhase1Objective = RevisedStore.newRow(myObjective.size());
        }

        int nbVariables = structure.countVariables();

        for (int j = 0; j < nbVariables; j++) {
            double sum = myConstraintsBody.aggregateColumn(base, j, Aggregator.SUM).doubleValue();
            myPhase1Objective.set(j, -sum);
        }

        //        double sum = myConstraintsRHS.aggregateRange(structure.nbIdty, m, Aggregator.SUM).doubleValue();
        //        myAlternativeValue = -sum;
    }

    @Override
    Primitive1D sliceBodyRow(final int i) {

        R064Store exclPart = RevisedStore.newColumn(n - m);

        this.doBodyRow(i, exclPart);

        Primitive1D retVal = Primitive1D.newInstance(n);
        for (int je = 0; je < excluded.length; je++) {
            retVal.set(excluded[je], exclPart.doubleValue(je));
        }
        return retVal;
    }

    @Override
    Primitive1D sliceDualVariables() {
        return new Primitive1D() {

            @Override
            public double doubleValue(final int index) {
                return -l.doubleValue(index);
            }

            @Override
            public void set(final int index, final double value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return m;
            }

        };
    }

}
