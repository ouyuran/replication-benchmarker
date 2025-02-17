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
package jbenchmarker.abt;

import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import collect.VectorClock.Causality;
import jbenchmarker.core.SequenceOperation.OpType;

/**
*
* @author Roh
*/
public class ABTLog {
	
	protected List<ABTOperation> Hi = new LinkedList<ABTOperation>();
	protected List<ABTOperation> Hd = new LinkedList<ABTOperation>();
	private   int 	sid;
	
	public ABTLog(int sid){
		this.sid = sid;
	}
	
	
	public ABTOperation updateHR(final ABTOperation op){
		
		ABTOperation o1 = null;
		ABTOperation o2 = null;
		List<ABTOperation> hih = new LinkedList<ABTOperation>();
		List<ABTOperation> hic = new LinkedList<ABTOperation>();
		
		convert2HC(op, Hi, hih, hic);
	
		o2 = ITSQ(op, hic);
		o1 = ITSQ(o2, Hd);
		
		if(o1==null){
			// do nothing
		} else if(o1.getType()==OpType.delete){
			Hd.add(o1);
		} else if(o1.getType()==OpType.insert){
			ListIterator<ABTOperation> li = Hd.listIterator();			
			ABTOperation ox = (ABTOperation)o2.clone();
			ABTOperation oy = (ABTOperation)o2.clone();
			ABTOperation oz = null;
			while(li.hasNext()){
				oz = li.next();
				oy.pos = ox.pos;
				IT(ox,oz);
				IT(oz,oy);		
			}
			Hi.add(o2);
		}
		return o1;
	}
	
	public ABTOperation updateHL(final ABTOperation op){
		ABTOperation tmp = (ABTOperation)op.clone();
		
		LinkedList<ABTOperation>	sHd  = new LinkedList<ABTOperation>();
		ListIterator<ABTOperation> 	liHd = Hd.listIterator(Hd.size());
		
		while(liHd.hasPrevious()){
			if(op.getType()==OpType.insert){
				ABTOperation top=(ABTOperation)liHd.previous().clone();
				swap(tmp, top, true);
				sHd.addFirst(top);
			} else {
				swap(tmp, (ABTOperation)liHd.previous(), false);
			}
		}
		
		if(op.getType()==OpType.delete){
			Hd.add(op);
		} else if(op.getType()==OpType.insert){
			Hi.add(tmp);
			Hd = sHd;
		}
		return tmp;
	}

	private static void convert2HC(ABTOperation op1, List<ABTOperation> H, List<ABTOperation> hh, List<ABTOperation> hc){		
		ListIterator<ABTOperation> li = H.listIterator();
		ABTOperation op2 = null;
		while(li.hasNext()){
			op2 = li.next();			
			switch(ABTOperation.getRelation(op1, op2)){
			case CO:
				hc.add(op2);
				break;
			case HA:
				ListIterator<ABTOperation> lihc=hc.listIterator(hc.size());
				while(lihc.hasPrevious()){
					swap(op2,lihc.previous(), true);
				}
				hh.add(op2);
				break;
			case HB:				
				throw new RuntimeException(op1.getReplica()+":"+op1.vc+"  "+op1.getReplica()+":"+op2.vc+": Causality violation");
			}
			 
		}
	}
	
		
	private static void swap(ABTOperation o1, ABTOperation op2, boolean realswap){
		ABTOperation o2;
		if(!realswap) o2=(ABTOperation)op2.clone();
		else o2=op2;
		int pos1=(o1.getType()==OpType.insert?o1.pos:o1.pos-1);
		int pos2=(o2.getType()==OpType.insert?o2.pos:o2.pos-1);
		if(pos1 > pos2){
			if(o2.getType()==OpType.insert) {
				o1.pos = o1.pos - 1;
			} else if(o2.getType()==OpType.delete){
				o1.pos = o1.pos + 1;
			}			
		} else if(pos1 == pos2){
			if(o2.getType()==OpType.delete &&
			   o1.getType()==OpType.delete){
				o1.pos = o1.pos+1;
			} else if(o1.getType()==OpType.delete &&
					  o2.getType()==OpType.insert){
				throw new RuntimeException("Not admissible swap");
			} else if(o1.getType()==OpType.insert &&
					  o2.getType()==OpType.insert){
				o2.pos = o2.pos + 1;
			} else { // o1 = ins, op2=del
				o2.pos = o2.pos +1;
			}
		} else { // o1.pos < op2.pos
			if(o1.getType()==OpType.insert){
				o2.pos = o2.pos + 1;
			} else { //o1=del
				o2.pos = o2.pos - 1;
			}
		}		
	}
	
	private ABTOperation IT(ABTOperation o1, final ABTOperation o2){
		int pos1=(o1.getType()==OpType.insert?o1.pos:o1.pos-1);
		int pos2=(o2.getType()==OpType.insert?o2.pos:o2.pos-1);
	
		if(pos2 < pos1){
			if(o2.getType()==OpType.insert){
				o1.pos = o1.pos + 1;
			} else if(o2.getType()==OpType.delete){
				o1.pos = o1.pos - 1;
			}
		} else if(pos2 == pos1){
			if(o2.getType()==OpType.insert && 
			   o1.getType()==OpType.delete){
				o1.pos = o1.pos + 1;
			} else if(o2.getType()==OpType.insert && 
					  o1.getType()==OpType.insert &&
					  o2.getReplica() < o1.getReplica()){
				o1.pos = o1.pos + 1;
			} else if(o2.getType()==OpType.delete &&
					  o1.getType()==OpType.delete){
				o1=null;
			}
		}		
		return o1;
	}
	
	//unproven code
	private ABTOperation ET(ABTOperation o1, ABTOperation o2){
		ABTOperation op = (ABTOperation)o1.clone();
		if(o2.pos < o1.pos){
			if(o2.getType()==OpType.insert){
				op.pos = op.pos - 1;
			} else if(o2.getType()==OpType.delete){
				op.pos = op.pos + 1;
			}
		} else if(o2.pos == o1.pos){
			if(o2.getType()==OpType.delete && 
			   o1.getType()==OpType.delete){
				
			} else if(o1.getType()==OpType.delete &&
					  o2.getType()==OpType.insert){				
				throw new RuntimeException("Not admissible transformation");
			}
		}
		return op;
	}
	
	private ABTOperation ITSQ(final ABTOperation op, final List<ABTOperation> H){
		ABTOperation o = (ABTOperation)op.clone();
		ListIterator<ABTOperation> li = H.listIterator();
		while(li.hasNext()){
			o=IT(o, li.next());
			if(o==null) {
				return o;
			}
		}
		return o;
	}	
	
	public void printHistory(List<ABTOperation> H){
		for(int i=H.size();i>0;i--){
			Formatter fmt = new Formatter();
			fmt.format("%4d", i);
			System.out.print("|"+fmt+":"+H.get(i-1)+"\t| ");
			System.out.println("\t"+H.get(i-1).getReplica()+":"+H.get(i-1).vc+"     ");			
		}
		if(H.size()>0) System.out.println("+-----------------------+");
	}
	
	public void printHistory(){
		
		int i=0;
		int max=Hi.size()>Hd.size()? Hi.size() : Hd.size();
		for(i=max;i>0;i--){
			Formatter fmt = new Formatter();
			fmt.format("%4d", i);
			if(i<=Hi.size()) System.out.print("|"+fmt+":"+Hi.get(i-1)+"\t| ");
			else System.out.print("|\t\t\t| ");
			if(i<=Hd.size()) System.out.print("|"+fmt+":"+Hd.get(i-1)+"\t|");
			else System.out.print("|\t\t\t|");
			if(i<=Hi.size()) System.out.print("\t"+Hi.get(i-1).getReplica()+":"+Hi.get(i-1).vc+"     ");
			else System.out.print("\t\t\t");
			if(i<=Hd.size()) System.out.print("\t"+Hd.get(i-1).getReplica()+":"+Hd.get(i-1).vc);
			System.out.println("");
		}
		System.out.println("+-----------------------+ +---------------------+");
		System.out.println("    Hi("+Hi.size()+") of site "+sid+"       "+"    Hd("+Hd.size()+") of site "+sid);		
	}
}
