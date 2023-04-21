package jbenchmarker.rgasplit;

import jbenchmarker.RDSL.RDSLWalkable;

import java.io.Serializable;
import java.util.List;


public class RgaSNode<T> implements Serializable, RDSLWalkable {

	private RgaSS3Vector key;		 
	private RgaSNode next;
	private RgaSNode link;
	
	private List<T> content;
	private int size;
	private boolean tomb;	//used for visible and tombstone purging if null, then not tombstone 




	/*
	 *		Constructors
	 */

	public RgaSNode(RgaSS3Vector key, RgaSNode next, RgaSNode link, List<T> c, boolean tomb) {
		this.key = key;
		this.next = next;
		this.link = link;
		this.content = c;
		if (content!=null) this.size = c.size();
		else this.size=0;
		this.tomb = tomb;
	}

	public RgaSNode() {
		this(null, null, null, null, true);
	}

	public RgaSNode(RgaSS3Vector s3v, List<T> c) {
		this(s3v, null, null, c, false);
	}

	public RgaSNode(RgaSNode n, List<T> c, int offset) {
		this(n.key.clone(), n.next, n.link, c, n.tomb);
		this.key.setOffset(offset);
	}

	public RgaSNode clone(){
		return new RgaSNode(key, next, link, content, tomb);
	}



	
	/*
	 *		toString, getContentAsString, equals, makeTombstone, getNextVisible, getLinkVisible and hashCode
	 */

	@Override
	public String toString() {
		String Next = next == null ? "null" : next.getKey().toString();
		String Link = link == null ? "null" : link.getKey().toString();
		return "[" + key + "," + Next + ","+ Link + ","+ tomb +","  + content + "," + size + "]";  
	}

	public String getContentAsString() {
		StringBuilder s = new StringBuilder();
		if (content!=null){
			for (T t : content) {

				s.append(t.toString());
			}
		}
		return s.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final RgaSNode other = (RgaSNode) obj;
		if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
			return false;
		}
		return true;
	}

	public void makeTombstone() {
		this.tomb = true;
		this.content = null;
	}

	public RgaSNode getNextVisible() {
		RgaSNode node = next;
		while (node != null && !node.isVisible()) {
			node = node.getNext();
		}
		return node;
	}
	
	public RgaSNode getLinkVisible() {
		RgaSNode node = next;
		while (node != null && !node.isVisible()) {
			node = node.getLink();
		}
		return node;
		
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + (this.key != null ? this.key.hashCode() : 0);
		return hash;
	}



	/*
	 *		Getters || Setters
	 */

	public RgaSS3Vector getKey() {
		return key;
	}

	public void setKey(RgaSS3Vector key) {
		this.key = key;
	}
	
	public int getOffset() {
		if (this.key!=null)	return key.getOffset();
		else return 0;
	}
	
	public void setOffset(int off) {
		if (this.key!=null) this.key.setOffset(off);
	}

	public RgaSNode getNext() {
		return next;
	}

	public void setNext(RgaSNode next) {
		this.next = next;
	}

	public RgaSNode getLink() {
		return link;
	}

	public void setLink(RgaSNode link) {
		this.link = link;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int size() {
//		return size;
		return content == null ? 0 : content.size();
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isVisible() {
		return !tomb;
	}

	public void setTomb(boolean tomb) {
		this.tomb = tomb;
	}

	public int getDistance(int level) {
		return size();
	}

	public String getContentString() {
		String s = "";
		for (T c: content) {
			s += c;
		}
		return s;
	}

	public RgaSNode<T> getRight(int level) {
		return next;
	}
}
