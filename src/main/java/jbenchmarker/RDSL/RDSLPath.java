package jbenchmarker.RDSL;

import jbenchmarker.rga.RGAMerge;
import jbenchmarker.rga.RGANode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class RDSLPath<T extends RDSLWalkable> {
    private LinkedList<RDSLNode<T>>[] levels;
    private LinkedList<T> level0;
    public static final int MAX_LEVEL = 32;

    private int totalLevels;

    public RDSLPath(int totalLevels) {
        this.totalLevels = totalLevels;
        this.levels = new LinkedList[totalLevels];
        for(int i = 0; i < totalLevels; i++) {
            levels[i] = new LinkedList<RDSLNode<T>>();
        }
        level0 = new LinkedList<T>();
    }

    public void add(RDSLNode<T> node, int level) {
        this.levels[level - 1].addLast(node);
    }

    public void addLevel0(T node) {
        this.level0.addLast(node);
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
            Iterator<RDSLNode<T>> iterator = this.levels[level - 1].iterator();
            if(iterator.hasNext()) iterator.next(); // skip the first item
            while(iterator.hasNext()) {
                totalDistance += iterator.next().getDistance(level);
            }
        } else {
            Iterator<T> iterator = this.level0.iterator();
            while(iterator.hasNext()) {
                totalDistance += iterator.next().getDistance(0);
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

    public RDSLNode getLastRDSLNodeOfLevel(int level) {
        if(level > this.totalLevels) return null;
        return this.levels[level - 1].getLast();
    }
    public T getLastDataNode() {
        return this.level0.getLast();
    }
}
