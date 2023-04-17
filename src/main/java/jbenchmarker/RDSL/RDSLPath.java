package jbenchmarker.RDSL;

import java.util.ArrayList;

public class RDSLPath {
    private ArrayList<RDSLFootPrint>[] levels;
    public static final int MAX_LEVEL = 255;
    private int dataNodesTotalDistance = 0;

    public RDSLPath() {
        this.levels = new ArrayList[MAX_LEVEL];
        for(int i = 0; i < MAX_LEVEL; i++) {
            levels[i] = new ArrayList<RDSLFootPrint>();
        }
    }

    public void add(RDSLFootPrint footPrint) {
        this.levels[footPrint.getLevel()].add(footPrint);
    }

    public void setDataNodesTotalDistance(int d) {
        this.dataNodesTotalDistance = d;
    }
    private int sumDistances(int level) {
        int totalDistance = 0;
        ArrayList<RDSLFootPrint> footPrints = this.levels[level];
        // first footprint should not be counted
        for(int i = 1; i < footPrints.size(); i++) {
            totalDistance += footPrints.get(i).getDistance();
        }
        return totalDistance;
    }

    public int getDistance(int level) {
        int total = 0;
        while(level >= 0) {
            total += this.sumDistances(level);
            level --;
        }
        return total + this.dataNodesTotalDistance;
    }
}
