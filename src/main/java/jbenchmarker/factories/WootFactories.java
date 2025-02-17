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
package jbenchmarker.factories;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.woot.WootMerge;
import jbenchmarker.woot.original.*;
import jbenchmarker.woot.wooth.*;
import jbenchmarker.woot.wooth.undo.WootHashReuse;
import jbenchmarker.woot.wooto.*;

/**
 *
 * @author urso
 */
public class WootFactories extends ReplicaFactory {
    public static class WootFactory extends ReplicaFactory {
        public MergeAlgorithm create(int r) {
            return new WootMerge(new WootOriginalDocument(), r);
        }
    }
    
    public static class WootHFactory extends ReplicaFactory {
        public MergeAlgorithm create(int r) {
            return new WootHashMerge(new WootHashDocument(), r);
        }
    }
    
    public static class WootUFactory extends ReplicaFactory {
        public MergeAlgorithm create(int r) {
            return new WootHashMerge(WootHashReuse.newDocument(), r);
        }
    }
    
    public static class WootOFactory extends ReplicaFactory {
        public MergeAlgorithm create(int r) {
            return new WootMerge(new WootOptimizedDocument(), r);
        }
    }
         
    @Override
    public MergeAlgorithm create(int r) {
        return new WootHashMerge(new WootHashDocument(), r);
    }    
}
