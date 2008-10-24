package lrf.merge;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Entrada {
	public static Entrada root=null;
	Entrada padre = null;
	String nombre = null;
	String rTipo[]=null;
	File file = null;
	ArrayList<Entrada> hijos = new ArrayList<Entrada>();

	public Entrada(Entrada padre, String nombre){
		this.padre = padre;
		this.nombre = nombre;
		this.rTipo=padre.rTipo;
		file = new File(padre.getFile(), nombre);
	}
	
	public Entrada(Entrada padre, String nombre, String resTypes[]) {
		if(padre==null)
			root=this;
		this.padre = padre;
		this.nombre = nombre;
		this.rTipo=resTypes;
		file = new File(nombre);
		ArrayList<String> res=new ArrayList<String>();
		recurse(file,res);
		for(int i=0;i<res.size();i++){
			String r=res.get(i);
			r=r.replace('\\','/');
			res.set(i,r.substring(nombre.length()));
		}
		for(int i=0;i<res.size();i++){
			String r=res.get(i);
			StringTokenizer st=new StringTokenizer(r,"/");
			Entrada padreInicial=this;
			while(st.hasMoreTokens()){
				String ename=st.nextToken();
				padreInicial=padreInicial.add(ename);
			}
		}
	}
	
	private Entrada add(String hijo){
		Entrada aux=null;
		for(int i=0;aux==null && i<hijos.size();i++){
			Entrada hija=hijos.get(i);
			if(hija.getNombre().equals(hijo))
				aux=hija;
		}
		if(aux!=null){
			return aux;
		}else{
			aux=new Entrada(this,hijo);
			hijos.add(aux);
			return aux;
		}
	}

	private void recurse(File f,ArrayList<String> resources){
		String all[]=f.list();
		for(int i=0;i<all.length;i++){
			File sf=new File(f,all[i]);
			if(!sf.isDirectory() && isSelectedResource(all[i])>-1){
				if(     sf.getAbsolutePath().toLowerCase().endsWith(".pdf")
					&& !sf.getName().startsWith(f.getName())){
					
					File nf=new File(f,f.getName()+"-"+sf.getName());
					sf.renameTo(nf);
					sf=nf;
				}
				resources.add(sf.getAbsolutePath());
			}
			if(sf.isDirectory())
				recurse(sf,resources);
		}
	}

	public String getNombrePresentable() {
		String procNombre = nombre;
		int pos = nombre.lastIndexOf("\\");
		if (pos != -1)
			procNombre = nombre.substring(pos + 1);
		int tr=isSelectedResource();
		if(tr>-1){
			procNombre=procNombre.substring(0,procNombre.length()-rTipo[tr].length());
		}
		if(padre!=null && procNombre.startsWith(padre.nombre)){
			procNombre=procNombre.substring(padre.nombre.length());
		}
		return procNombre;
	}

	public String getCanonicalName() {
		if (getPadre() != null)
			return getPadre().getCanonicalName() + "/" + getNombre();
		return getNombrePresentable();
	}

	public String getNombre() {
		return nombre;
	}

	public File getFile() {
		return file;
	}
	
	public Entrada getPadre() {
		return padre;
	}

	public boolean isDir() {
		return file.isDirectory();
	}

	public int isSelectedResource() {
		return isSelectedResource(nombre);
	}

	public int isSelectedResource(String name) {
		for(int i=0;i<rTipo.length;i++)
			if(name.toLowerCase().endsWith(rTipo[i].toLowerCase()))
				return i;
		return -1;
	}

	public ArrayList<Entrada> getHijos() {
		return hijos;
	}
}
