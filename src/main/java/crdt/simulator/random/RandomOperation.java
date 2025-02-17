/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package crdt.simulator.random;

import collect.VectorClock;
import crdt.CRDT;
import crdt.simulator.TraceOperation;
import crdt.tree.orderedtree.OrderedTreeOperation;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import jbenchmarker.core.LocalOperation;
import crdt.Operation;

/**
 *
 * @author urso
 * @author Stephane Martin <stephane.martin@loria.fr>
 */
public class RandomOperation extends TraceOperation implements Serializable {

    transient final private OperationProfile opp;
    private LocalOperation op = null;

    

    class RadomOp implements LocalOperation, Serializable {

        //private LocalOperation op = null;
        public RadomOp() {
            //this.opp = opp;
        }

        @Override
        public LocalOperation adaptTo(CRDT replica) {
            if (op == null) {
                op = opp.nextOperation(replica);
                return op;
            } else {
                return op.adaptTo(replica);
            }

        }

        @Override
        public Operation clone() {
            try {
                return (RadomOp) super.clone();
            } catch (Exception ex) {
                Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    public RandomOperation(OperationProfile opp, int replica, VectorClock VC) {
        super(replica, VC);
        this.opp = opp;
        //this.op=opp.nextOperation(replica, getVectorClock());
    }
    
    @Override
    public RandomOperation clone() {
        try {
            return (RandomOperation) super.clone();
        } catch (Exception ex) {
            Logger.getLogger(OrderedTreeOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return "RandomOperation{op=" + op + ", VC=" + this.getVectorClock() + " ,N°Rep=" + this.getReplica() + '}';
    }

    @Override
    public LocalOperation getOperation() {
        return new RadomOp();
    }
}
