package coffeeblocks.foundation.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Vector3d;

import coffeeblocks.general.FileImporter;
import coffeeblocks.general.VectorTools;
import coffeeblocks.opengl.components.CoffeeMaterial;
import coffeeblocks.opengl.components.CoffeeVertex;

public class WavefrontModelReader implements ModelReader{
	
	private List<Float> model = new ArrayList<>();
	
	private List<List<Integer>> faces = new ArrayList<>();
	
	private List<List<Float>> vertices = new ArrayList<>();
	private List<List<Float>> texCoords = new ArrayList<>();
	private List<List<Float>> normals = new ArrayList<>();
	
	public List<List<Float>> getVertices(){
		return vertices;
	}
	public List<List<Integer>> getIndices(){
		return faces;
	}
	
	private ModelContainer object = null;

	@Override
	public void setTargetObject(ModelContainer object){
		if(object!=null)
			this.object = object;
	}
	
	private final int vertexOffset = 3;
	
	@Override
	public List<Float> interpretFile(List<String> data, String filename) {
		// TODO Auto-generated method stub
		
		List<String> mtls = new ArrayList<>();
		List<String> usedmtl = new ArrayList<>();
		{
			Iterator<String> it = data.iterator();
			String itv;
			while(it.hasNext()){
				itv = it.next();
				if(itv.startsWith("mtllib ")){
					mtls.add(itv.split(" ")[1]);
					continue;
				}
				if(itv.startsWith("usemtl ")){
					usedmtl.add(itv.split(" ")[1]);
					continue;
				}
				interpretLine(itv);
			}
		}
		for(String mtlfile : mtls)
			for(String mtl : usedmtl)
				parseMtlFile(FileImporter.getBasename(filename)+"/"+mtlfile,mtl);

		List<Float> emptyTC0 = new ArrayList<>();
		emptyTC0.add(0.0f);
		emptyTC0.add(0.0f);
		List<Float> emptyTC1 = new ArrayList<>();
		emptyTC1.add(0.5f);
		emptyTC1.add(1.0f);
		List<Float> emptyTC2 = new ArrayList<>();
		emptyTC2.add(1.0f);
		emptyTC2.add(0.5f);
		List<Float> emptyNormal = new ArrayList<>();
		emptyNormal.add(1f);
		emptyNormal.add(1f);
		emptyNormal.add(1f);
		
		for(List<Integer> face : faces){
			CoffeeVertex vert = new CoffeeVertex();
			for(int i=0;i<vertexOffset;i++){
				int texCoordOffset = vertexOffset+i;
				int normalOffset = vertexOffset*2+i;
				model.addAll(vertices.get(face.get(i)-1));
				if(face.get(texCoordOffset)!=0&&texCoords.size()>=face.get(texCoordOffset)){
					model.addAll(texCoords.get(face.get(texCoordOffset)-1));
				}else{
					if(i==0)
						model.addAll(emptyTC0);
					if(i==1)
						model.addAll(emptyTC1);
					if(i==2)
						model.addAll(emptyTC2);
				}
				if(face.get(normalOffset)!=0&&normals.size()>=face.get(normalOffset)){
					model.addAll(normals.get(face.get(normalOffset)-1));
				}else
					model.addAll(emptyNormal);
			}
		}
		return model;
	}
	
	private void parseMtlFile(String filename, String material){
		if(object==null)
			return; //We cannot do anything
		List<String> data = FileImporter.readFile(filename);
		
		CoffeeMaterial mtl = object.getMaterial();
		
		for(String line : data){
//			if(line.startsWith("Ka "))
//				mtl.ambientColor = parseVector(line.substring(3,line.length()));
			if(line.startsWith("Ns "))
				mtl.setShininess(Float.valueOf(line.substring(3,line.length())));
			if(line.startsWith("Ks "))
				mtl.setSpecularColor(VectorTools.parseStrVector(line.substring(3,line.length())," "));
			if(line.startsWith("map_Kd "))
				mtl.setDiffuseTexture(filename.substring(0, filename.lastIndexOf('/'))+"/"+line.substring(7,line.length()));
			if(line.startsWith("map_Ks "))
				mtl.setSpecularTexture(filename.substring(0, filename.lastIndexOf('/'))+"/"+line.substring(7,line.length()));
			if(line.startsWith("map_Ns "))
				mtl.setHighlightTexture(filename.substring(0, filename.lastIndexOf('/'))+"/"+line.substring(7,line.length()));
			if(line.startsWith("map_d "))
				mtl.setTransparencyTexture(filename.substring(0, filename.lastIndexOf('/'))+"/"+line.substring(6,line.length()));
			if(line.startsWith("map_Bump "))
				mtl.setBumpTexture(filename.substring(0, filename.lastIndexOf('/'))+"/"+line.substring(9,line.length()));
			if(line.startsWith("bump "))
				mtl.setBumpTexture(filename.substring(0, filename.lastIndexOf('/'))+"/"+line.substring(5,line.length()));
//			if(line.startsWith("Kd "))
//				mtl.diffuseColor = parseVector(line.substring(3,line.length()));
//			if(line.startsWith("d "))
//				mtl.dissolution = Float.valueOf(line.substring(2, line.length()));
		}
	}
	
	private void interpretLine(String data){
		if(data.startsWith("#"))
			return;
		if(data.startsWith("f "))
			interpretFace(data);
		if(data.startsWith("v "))
			interpretVert(data);
		if(data.startsWith("vt "))
			interpretTexCoord(data);
		if(data.startsWith("vn "))
			interpretNormal(data);
	}
	
	private void interpretTexCoord(String coord){
		List<Float> coords = new ArrayList<>();
		for(String value : coord.substring(3, coord.length()).split(" ")){
			if(coords.size()==1)
				coords.add(1f-Float.valueOf(value));
			else
				coords.add(Float.valueOf(value));
		}
		texCoords.add(coords);
	}
	private void interpretVert(String vert){
		List<Float> verts = new ArrayList<>();
		for(String value : vert.substring(2, vert.length()).split(" "))
			verts.add(Float.valueOf(value));
		vertices.add(verts);
	}
	private void interpretNormal(String normal){
		List<Float> normalList = new ArrayList<>();
		for(String value : normal.substring(3, normal.length()).split(" "))
			normalList.add(Float.valueOf(value));
		normals.add(normalList);
	}
	private void interpretFace(String face){
		List<Integer> vertices = new ArrayList<>();
		List<Integer> tex = new ArrayList<>();
		List<Integer> normals = new ArrayList<>();
		for(String value : face.substring(2, face.length()).split(" ")){
			String[] values = value.split("/");
			for(int i=0;i<values.length;i++){
				if(i==0)
					if(!values[i].isEmpty())
						vertices.add(Integer.valueOf(values[i]));
				if(i==1)
					if(!values[i].isEmpty())
						tex.add(Integer.valueOf(values[i]));
				if(i==2)
					if(!values[i].isEmpty())
						normals.add(Integer.valueOf(values[i]));
			}
		}
		if(tex.size()==0){
			tex.add(0);
			tex.add(0);
			tex.add(0);
		}
		if(normals.size()==0){
			normals.add(0);
			normals.add(0);
			normals.add(0);
		}
		vertices.addAll(tex);
		vertices.addAll(normals);
		this.faces.add(vertices);
	}
}
