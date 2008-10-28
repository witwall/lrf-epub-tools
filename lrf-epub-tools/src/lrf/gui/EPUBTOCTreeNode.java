package lrf.gui;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Node;

public class EPUBTOCTreeNode extends BaseNode{
	EPUBTreeNode raiz;
	String tocName;
	Vector<Node> childs=new Vector<Node>();

	public EPUBTOCTreeNode(EPUBTreeNode r, String tn, String hr, Vector<Node> cs){
		raiz=r;
		tocName=tn;
		href=hr;
		childs=cs;
	}

	public String toString(){
		return tocName;
	}
	
	public String getVal(){
		return href;
	}
	
	public void populate(DefaultMutableTreeNode treeNode){
		for(int i=0;i<childs.size();i++){
			raiz.populateNode(treeNode, childs.elementAt(i).getFirstChild());
		}
	}
}
