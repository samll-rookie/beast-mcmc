package dr.math;

import dr.math.matrixAlgebra.ReadableVector;
import dr.math.matrixAlgebra.WrappedVector;

/**
 * @author Marc A. Suchard
 * @author Guy Baele
 * @author Xiang Ji
 */
public interface AdaptableVector {

    public ReadableVector getMean();

    public int getUpdateCount();

    public void update(ReadableVector x);

    class Default implements AdaptableVector {
        final private int dim;
        final private double[] oldMeans;
        final private double[] newMeans;

        private int updates;

        public Default(int dim) {
            this.dim = dim;
            this.oldMeans = new double[dim];
            this.newMeans = new double[dim];

            updates = 0;
        }

        @Override
        public ReadableVector getMean() {
            return new WrappedVector.Raw(newMeans);
        }

        @Override
        public int getUpdateCount() {
            return updates;
        }

        @Override
        public void update(ReadableVector x) {
            for (int i = 0; i < dim; i++) {
                oldMeans[i] = newMeans[i];
                newMeans[i] = ((oldMeans[i] * (updates - 1)) + x.get(i)) / updates;
            }
        }

        public double getOldMeans(int index) {
            return oldMeans[index];
        }

        public double getNewMeans(int index) {
            return newMeans[index];
        }
    }

    class LimitedMemory implements AdaptableVector {

        final private int dim;
        final private double[][] meanQueue;
        private double[] mean;
        final private int queueSize;
        private int updateIndex;
        private int updates;

        public LimitedMemory(int dim, int queueSize) {
            this.dim = dim;
            this.queueSize = queueSize;
            this.meanQueue = new double[queueSize][dim];
            this.mean = new double[dim];
            this.updateIndex = 0;
            this.updates = 0;
        }

        @Override
        public ReadableVector getMean() {
            return new WrappedVector.Raw(mean);
        }

        @Override
        public int getUpdateCount() {
            return updates;
        }

        @Override
        public void update(ReadableVector x) {
            for (int i = 0; i < dim; i++) {
                double increment = (x.get(i) - meanQueue[updateIndex][i]) / ((double) queueSize);
                mean[i] += increment;
                meanQueue[updateIndex][i] = x.get(i);
            }
            updates++;
            updateIndex = (updateIndex + 1) % queueSize;
        }
    }

}
