package jbenchmarker.RDSL;

import jbenchmarker.rga.RGAMerge;
import jbenchmarker.rga.RGANode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class RDSLPath<T extends RDSLWalkable> {
    private LinkedList<RDSLFootPrint<RDSLNode<T>>>[] levels;
    private LinkedList<RDSLFootPrint<T>> level0;
    public static final int MAX_LEVEL = 32;

    private int totalLevels;

    public RDSLPath(int totalLevels) {
        this.totalLevels = totalLevels;
        this.levels = new LinkedList[totalLevels];
        for(int i = 0; i < totalLevels; i++) {
            levels[i] = new LinkedList<RDSLFootPrint<RDSLNode<T>>>();
        }
        level0 = new LinkedList<RDSLFootPrint<T>>();
    }

    public void add(RDSLFootPrint<RDSLNode<T>> footPrint) {
        this.levels[footPrint.getLevel() - 1].addLast(footPrint);
    }

    public void addLevel0(RDSLFootPrint<T> footPrint) {
        this.level0.addLast(footPrint);
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
        if(level > this.totalLevels) return 0;
        if(level > 0) {
//            int index = Math.min(this.totalLevels, level) - 1;
            Iterator<RDSLFootPrint<RDSLNode<T>>> iterator = this.levels[level - 1].iterator();
            if(iterator.hasNext()) iterator.next(); // skip the first item
            while(iterator.hasNext()) {
                totalDistance += iterator.next().getDistance();
            }
        } else {
            Iterator<RDSLFootPrint<T>> iterator = this.level0.iterator();
            while(iterator.hasNext()) {
                totalDistance += iterator.next().getDistance();
            }
        }
        return totalDistance;
    }

    public int getDistance(int level) {
//        level = Math.min(this.totalLevels, level);
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
            LinkedList<RDSLFootPrint<RDSLNode<T>>> list = this.levels[level - 1];
            return list.getLast();
        } else {
            return this.level0.getLast();
        }
    }
    public T getLastDataNode() {
        RDSLFootPrint<T> f = getLastFootPrintOfLevel(0);
        return f.getNode();
    }
}
