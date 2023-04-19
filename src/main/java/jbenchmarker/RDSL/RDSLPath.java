package jbenchmarker.RDSL;

import jbenchmarker.rga.RGAMerge;
import jbenchmarker.rga.RGANode;

import java.util.ArrayList;

public class RDSLPath<T extends RDSLWalkable> {
    private ArrayList<RDSLFootPrint<RDSLNode<T>>>[] levels;
    private ArrayList<RDSLFootPrint<T>> level0;
    public static final int MAX_LEVEL = 32;

    private int totalLevels;

    public RDSLPath(int totalLevels) {
        this.totalLevels = totalLevels;
        this.levels = new ArrayList[totalLevels];
        for(int i = 0; i < totalLevels; i++) {
            levels[i] = new ArrayList<RDSLFootPrint<RDSLNode<T>>>();
        }
        level0 = new ArrayList<RDSLFootPrint<T>>();
    }

    public void add(RDSLFootPrint<RDSLNode<T>> footPrint) {
        this.levels[footPrint.getLevel() - 1].add(footPrint);
    }

    public void addLevel0(RDSLFootPrint<T> footPrint) {
        this.level0.add(footPrint);
    }

    private int sumDistances(int level) {
        // ---a--------x=3+c.getDistance()+d.getDistance() (b should not be counted)
        //    |        |
        //    b--c--d--3(still 3, e should not be counted)
        //          |  |
        //          e--3(fgh)
        //          |  |
        //          fghi
        // the path for x is
        // [a]
        // [b, c, d]
        // [e]
        // [f, g, h]
        // this first skip item for each level (a, b, e) should not be counted
        // while first data item should be counted
        int totalDistance = 0;
        if(level < this.totalLevels) {
            if(level > 0) {
                ArrayList<RDSLFootPrint<RDSLNode<T>>> footPrints = this.levels[level - 1];
                for(int i = 1; i < footPrints.size(); i++) {
                    totalDistance += footPrints.get(i).getDistance();
                }
            } else {
                ArrayList<RDSLFootPrint<T>> footPrints = this.level0;
                for(int i = 0; i < footPrints.size(); i++) {
                    totalDistance += footPrints.get(i).getDistance();
                }
            }
        }
        return totalDistance;
    }

    public int getDistance(int level) {
        // should count from level - 1 to 0
        level--;
        int total = 0;
        while(level >= 0) {
            total += this.sumDistances(level);
            level --;
        }
        return total;
    }

    public RDSLFootPrint getLastFootPrintOfLevel(int level) {
        if(level > this.totalLevels) return null;
        if(level > 0) {
            ArrayList<RDSLFootPrint<RDSLNode<T>>> list = this.levels[level - 1];
            return list.get(list.size() - 1);
        } else {
            return this.level0.get(this.level0.size() - 1);
        }
    }
    public T getLastDataNode() {
        RDSLFootPrint<T> f = getLastFootPrintOfLevel(0);
        return f.getNode();
    }
}
