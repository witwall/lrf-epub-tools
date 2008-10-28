package lrf.gui;

import java.io.File;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lrf.Utils;
import lrf.epub.EPUBDoc;

public class EPUBTreeNode extends BaseNode{
	EPUBDoc ref=null;

	public EPUBTreeNode(File f){
		file=f;
	}

	public String toString(){
		return file.getName();
	}
	
	public void populate(DefaultMutableTreeNode treeNode){
		if(ref==null){
			try {
				ref=EPUBDoc.load(file);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		NodeList nl;
		try {
			nl = ref.getNavPoints();
		} catch (XPathExpressionException e) {
			return;
		}
		for(int i=0;i<nl.getLength();i++){
			Node n=nl.item(i).getFirstChild();
			populateNode(treeNode, n);
		}
		
	}

	public void populateNode(DefaultMutableTreeNode treeNode, Node n) {
		String tocName=null;
		String  href=null;
		Vector<Node> navPointChilds=new Vector<Node>();
		while(n!=null){
			if(n.getNodeType()==Node.TEXT_NODE){
				n=n.getNextSibling();
				continue;
			}
			String localName=n.getLocalName();
			if(localName.equals("navLabel")){
				for(Node m=n.getFirstChild();m!=null;m=m.getNextSibling()){
					if(m.getNodeType()==Node.TEXT_NODE)
						continue;
					if(m.getLocalName().equals("text")){
						tocName=Utils.toUnhandText(m.getTextContent().trim());
						break;
					}
				}
			}
			if(localName.equals("content")){
				href=ref.getOPFDir()+n.getAttributes().getNamedItem("src").getNodeValue();
			}
			if(localName.equals("navPoint")){
				navPointChilds.add(n);
			}
			n=n.getNextSibling();
		}
		EPUBTOCTreeNode etn=new EPUBTOCTreeNode(this, tocName,href,navPointChilds);
		DefaultMutableTreeNode sub=new DefaultMutableTreeNode(etn);
		if(navPointChilds.size()>0)
			sub.add(new DefaultMutableTreeNode("Fake"));
		treeNode.add(sub);
	}
	
}
