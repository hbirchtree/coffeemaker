package coffeeblocks.foundation.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import coffeeblocks.general.FileImporter;
import coffeeblocks.general.VectorTools;
import coffeeblocks.opengl.components.CoffeeMaterial;

public class WavefrontModelReader implements ModelReader{
	
	public WavefrontModelReader(){
		emptyTC0.add(0.0f);
		emptyTC0.add(0.0f);
		emptyTC1.add(0.5f);
		emptyTC1.add(1.0f);
		emptyTC2.add(1.0f);
		emptyTC2.add(0.5f);
		emptyNormal.add(1f);
		emptyNormal.add(1f);
		emptyNormal.add(1f);
	}
	
	@Override
	public Map<String,ModelIntermediate> getModels(){
		return models;
	}
	@Override
	public Map<String,CoffeeMaterial> getMaterials(){
		return materials;
	}
	
	private Map<String,CoffeeMaterial> materials = new HashMap<>();
	private Map<String,ModelIntermediate> models = new HashMap<>();
	private Map<String,Integer> vertOffset = new HashMap<>();
	private Map<String,Integer> texcOffset = new HashMap<>();
	private Map<String,Integer> normOffset = new HashMap<>();
	
	private final int vertexOffset = 3;
	
	@Override
	public ModelIntermediate interpretFile(List<String> data, String filename) {
		// TODO Auto-generated method stub
		
		{
			Iterator<String> it = data.iterator();
			String itv;
			int vertexCount = 0;
			int normalCount = 0;
			int texcrdCount = 0;
			while(it.hasNext()){
				itv = it.next();
				if(itv.startsWith("mtllib ")){
					parseMtlFile(FileImporter.getBasename(filename)+itv.substring(7));
					continue;
				}
				else while(itv.startsWith("o ")&&it.hasNext()){
					ModelIntermediate model = new ModelIntermediate();
					model.name = itv.substring(2);
					while(it.hasNext()){
						itv = it.next();
						interpretLine(itv,model);
						if(itv.startsWith("o "))
							break;
					}
					models.put(model.name, model);
					vertOffset.put(model.name, vertexCount);
					texcOffset.put(model.name, texcrdCount);
					normOffset.put(model.name, normalCount);
					vertexCount+=model.vertices.size();
					normalCount+=model.normals.size();
					texcrdCount+=model.texCoords.size();
				}
			}
		}
		models.values().stream().forEach(e -> {
			processModel(e,vertOffset.get(e.name),texcOffset.get(e.name),normOffset.get(e.name));
			e.material = (e.mtl==null) ? new CoffeeMaterial() : materials.get(e.mtl);
		});

		return models.values().stream().collect(Collectors.toList()).get(0);
	}
	

	private List<Float> emptyTC0 = new ArrayList<>();
	private List<Float> emptyTC1 = new ArrayList<>();
	private List<Float> emptyTC2 = new ArrayList<>();
	private List<Float> emptyNormal = new ArrayList<>();
	
	private void processModel(ModelIntermediate target, int vertOffset,int texcOffset,int normOffset){
		List<List<Integer>> faces = target.faces;
		List<List<Float>> vertices = target.vertices;
		List<List<Float>> texCoords = target.texCoords;
		List<List<Float>> normals = target.normals;
		List<Float> model = target.model;
		
		for(List<Integer> face : faces){
			for(int i=0;i<vertexOffset;i++){
				int texCoordOffset = vertexOffset+i;
				int normalOffset = vertexOffset*2+i;
				model.addAll(vertices.get(face.get(i)-1-vertOffset));
				if(face.get(texCoordOffset)!=0&&texCoords.size()>=face.get(texCoordOffset)-texcOffset){
					model.addAll(texCoords.get(face.get(texCoordOffset)-1-texcOffset));
				}else{
					if(i==0)
						model.addAll(emptyTC0);
					if(i==1)
						model.addAll(emptyTC1);
					if(i==2)
						model.addAll(emptyTC2);
				}
				if(face.get(normalOffset)!=0&&normals.size()>=face.get(normalOffset)-normOffset){
					model.addAll(normals.get(face.get(normalOffset)-1-normOffset));
				}else
					model.addAll(emptyNormal);
			}
		}
	}
	
	private void parseMtlFile(String filename){
		List<String> data = FileImporter.readFile(filename);
		
		Iterator<String> it = data.iterator();
		String line;
		while(it.hasNext()){
			line = it.next();
//			if(line.startsWith("Ka "))
			//				mtl.ambientColor = parseVector(line.substring(3,line.length()));
			while(line.startsWith("newmtl ")){
				String name = line.substring(7);
				CoffeeMaterial mtl = new CoffeeMaterial();
				while(it.hasNext()){
					line = it.next();
					if(line.startsWith("Ns "))
						mtl.setShininess(Float.valueOf(line.substring(3,line.length())));
					if(line.startsWith("Ks "))
						mtl.setSpecularColor(VectorTools.parseStrVector(line.substring(3,line.length())," "));
					if(line.startsWith("map_Kd "))
						mtl.setDiffuseTexture(FileImporter.getBasename(filename)+line.substring(7,line.length()));
					if(line.startsWith("map_Ks "))
						mtl.setSpecularTexture(FileImporter.getBasename(filename)+line.substring(7,line.length()));
					if(line.startsWith("map_Ns "))
						mtl.setHighlightTexture(FileImporter.getBasename(filename)+line.substring(7,line.length()));
					if(line.startsWith("map_d "))
						mtl.setTransparencyTexture(FileImporter.getBasename(filename)+line.substring(6,line.length()));
					if(line.startsWith("map_Bump "))
						mtl.setBumpTexture(FileImporter.getBasename(filename)+line.substring(9,line.length()));
					if(line.startsWith("bump "))
						mtl.setBumpTexture(FileImporter.getBasename(filename)+line.substring(5,line.length()));
					if(line.startsWith("newmtl "))
						break;
				}
				materials.put(name, mtl);
			}
//			if(line.startsWith("Kd "))
//				mtl.diffuseColor = parseVector(line.substring(3,line.length()));
//			if(line.startsWith("d "))
//				mtl.dissolution = Float.valueOf(line.substring(2, line.length()));
		}
	}
	
	private void interpretLine(String data,ModelIntermediate model){
		if(data.startsWith("#"))
			return;
		else if(data.startsWith("usemtl "))
			model.mtl = data.substring(7);
		else if(data.startsWith("f "))
			interpretFace(data,model.faces);
		else if(data.startsWith("v "))
			interpretVert(data,model.vertices);
		else if(data.startsWith("vt "))
			interpretTexCoord(data,model.texCoords);
		else if(data.startsWith("vn "))
			interpretNormal(data,model.normals);
	}
	
	private void interpretTexCoord(String coord, List<List<Float>> texCoords){
		List<Float> coords = new ArrayList<>();
		for(String value : coord.substring(3, coord.length()).split(" ")){
			if(coords.size()==1)
				coords.add(1f-Float.valueOf(value));
			else
				coords.add(Float.valueOf(value));
		}
		texCoords.add(coords);
	}
	private void interpretVert(String vert, List<List<Float>> vertices){
		List<Float> verts = new ArrayList<>();
		for(String value : vert.substring(2, vert.length()).split(" "))
			verts.add(Float.valueOf(value));
		vertices.add(verts);
	}
	private void interpretNormal(String normal,List<List<Float>> normals){
		List<Float> normalList = new ArrayList<>();
		for(String value : normal.substring(3, normal.length()).split(" "))
			normalList.add(Float.valueOf(value));
		normals.add(normalList);
	}
	private void interpretFace(String face,List<List<Integer>> faces){
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
		faces.add(vertices);
	}
}
