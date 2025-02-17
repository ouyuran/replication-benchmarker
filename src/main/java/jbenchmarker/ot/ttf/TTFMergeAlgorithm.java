/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.ot.ttf;

import collect.VectorClock;
import crdt.CRDT;
import crdt.Factory;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.List;
import jbenchmarker.core.*;
import jbenchmarker.ot.soct2.OTAlgorithm;
import jbenchmarker.ot.soct2.OTMessage;
import jbenchmarker.ot.soct2.OTReplica;
import jbenchmarker.ot.soct2.SOCT2;
import jbenchmarker.ot.soct2.SOCT2TranformationInterface;

/**
 * This TTF Merge Algorithm uses SOCT2 algorithm with TTF method
 *
 * @author oster urso
 */
public class TTFMergeAlgorithm<O extends TTFOperation> extends MergeAlgorithm implements OTReplica<String, O> {

    final private OTAlgorithm<O> otAlgo;

    public OTAlgorithm<O> getOtAlgo() {
        return otAlgo;
    }

    /**
     * Make new TTFMerge algorithm with docuement (TTFDocument) and site id or
     * replicat id.
     *
     * @param doc TTF Document
     * @param siteId SiteID
     */
    public TTFMergeAlgorithm(TTFDocument doc, int siteId, Factory<OTAlgorithm<O>> otAlgo) {
        super(doc);
        this.otAlgo = otAlgo.create();
        setReplicaNumber(siteId);
    }

    /**
     * Default SOCT2 Factory
     *
     */
    public TTFMergeAlgorithm(int siteId) {
        this(new TTFDocument(), siteId, new SOCT2<O>(new TTFTransformations(), siteId, null));
        setReplicaNumber(siteId);
    }

    public TTFMergeAlgorithm(Factory<OTAlgorithm<O>> otAlgo) {
        this(new TTFDocument(), 0, otAlgo);
    }
    
    @Override
    public TTFDocument getDoc() {
        return (TTFDocument) super.getDoc(); //To change body of generated methods, choose Tools | Templates.
    }
   
    /**
     * @return Vector Clock of site
     */
    public VectorClock getClock() {
        return otAlgo.getSiteVC();
    }

    protected TTFOperation deleteOperation(int pos) {
        return new TTFOperationWithId(SequenceOperation.OpType.delete, pos, null, getReplicaNumber());
    }

    protected TTFOperation insertOperation(int pos, Object content) {
        return new TTFOperationWithId(SequenceOperation.OpType.insert, pos, content, getReplicaNumber());
    }

    /*
     * This integrate local modifications and generate message to another
     * replicas
     */
    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        TTFDocument doc = this.getDoc();
        List<Operation> generatedOperations = new ArrayList<Operation>();

        int mpos = doc.viewToModel(opt.getPosition());
        int visibleIndex = 0;
        for (int i = 0; i < opt.getLenghOfADel(); i++) {
            // TODO: could be improved with an iterator on only visible characters
            while (!doc.getChar(mpos + visibleIndex).isVisible()) {
                visibleIndex++;
            }
            Operation op = deleteOperation(mpos + visibleIndex);
            generatedOperations.add(new TTFSequenceMessage(otAlgo.estampileMessage(op)));
            doc.apply(op);
        }
        return generatedOperations;
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        TTFDocument doc = this.getDoc();
        List<Operation> generatedOperations = new ArrayList<Operation>();

        int mpos = doc.viewToModel(opt.getPosition());
        for (int i = 0; i < opt.getContent().size(); i++) {
            TTFOperation op = insertOperation(mpos, opt.getContent().get(i));
            mpos = op.getPosition() + 1;
            generatedOperations.add(new TTFSequenceMessage(otAlgo.estampileMessage(op)));
            doc.apply(op);
        }
        return generatedOperations;
    }

    /**
     * Make a new mergeAlgorithm with 0 as site id.
     *
     * @return new TTFMergeAlgorithm
     */
    @Override
    public CRDT<String> create() {
        return new TTFMergeAlgorithm(new TTFDocument(), 0, otAlgo);
    }

    /*
     * IntegrateRemote Operation
     */
    @Override
    protected void integrateRemote(crdt.Operation message) throws IncorrectTraceException {
        integrateOneOperation(((TTFSequenceMessage) message).getSoct2Message());
    }
    
    private void integrateOneOperation(OTMessage mess) {
        
        Operation op = otAlgo.integrateRemote(mess);        
        //=======================================================
        computation();
        if (((TTFOperation) op).getType() == SequenceOperation.OpType.noop) {
            nbrCleanMerge++;
        }
        //=======================================================
        this.getDoc().apply(op);
    }
    
    private void computation() {
        this.nbrInsConcur = otAlgo.getLog().nbrInsConcur;
        this.nbrInsDelConcur = otAlgo.getLog().nbrInsDelConcur;
        this.nbrDelDelConcur = otAlgo.getLog().nbrDelDelConcur;
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        otAlgo.setReplicaNumber(replicaNumber);
    }

    @Override
    public SOCT2TranformationInterface<O> getTransformation() {
        return otAlgo.getTransformation();
    }
    
}
