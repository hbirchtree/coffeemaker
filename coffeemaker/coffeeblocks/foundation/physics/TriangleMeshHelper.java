package coffeeblocks.foundation.physics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import coffeeblocks.foundation.models.WavefrontModelReader;
import coffeeblocks.general.FileImporter;

import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.util.ObjectArrayList;

public class TriangleMeshHelper {
	private final static int VERTEX_STRIDE = 4*3;
	private final static int INDEX_STRIDE = 3*4;
	public static BvhTriangleMeshShape createTriangleMesh(String meshFile,Vector3f scale){
		if(meshFile==null)
			return null;
		//Vi utnytter noe av funksjonaliteten ved Wavefront-parseren, å ta ut indeksering og vertex-data.
		List<String> meshData = FileImporter.readFile(meshFile);
		if(meshData==null)
			return null;
		WavefrontModelReader reader = new WavefrontModelReader();
		reader.interpretFile(meshData, meshFile);
		
		List<List<Integer>> indices = reader.getIndices();
		List<List<Float>> vertices = reader.getVertices();
		
		ByteBuffer verticesB = ByteBuffer.allocateDirect(vertices.size()*VERTEX_STRIDE).order(ByteOrder.nativeOrder());
		ByteBuffer indicesB = ByteBuffer.allocateDirect(indices.size()*INDEX_STRIDE).order(ByteOrder.nativeOrder());
		
		List<Float> scalar = new ArrayList<>();
		scalar.add(scale.x);
		scalar.add(scale.y);
		scalar.add(scale.z);
		
		for(List<Float> vert : vertices)
			for(int i=0;i<3;i++)
				verticesB.putFloat(vert.get(i)*scalar.get(i));
		for(List<Integer> indx : indices)
			for(int i=2;i>-1;i--)
				indicesB.putInt(indx.get(i)-1);
		
		indicesB.flip();
		TriangleIndexVertexArray mesh = new TriangleIndexVertexArray(indices.size(),indicesB,INDEX_STRIDE,vertices.size(),verticesB,VERTEX_STRIDE);
		
		return new BvhTriangleMeshShape(mesh,true,true);
	}
	public static ConvexHullShape createConvexMesh(String meshFile, Vector3f scale){
		if(meshFile==null)
			return null;
		//Vi utnytter noe av funksjonaliteten ved Wavefront-parseren, å ta ut indeksering og vertex-data.
		List<String> meshData = FileImporter.readFile(meshFile);
		if(meshData==null)
			return null;
		WavefrontModelReader reader = new WavefrontModelReader();
		reader.interpretFile(meshData, meshFile);
		
		List<List<Float>> vertices = reader.getVertices();
		
		ObjectArrayList<Vector3f> pointCloud = new ObjectArrayList<>();
		int total_i = 0;
		while(total_i<vertices.size()){
			Vector3f point = new Vector3f();
			point.x = vertices.get(total_i).get(0)*scale.x;
			point.y = vertices.get(total_i).get(1)*scale.y;
			point.z = vertices.get(total_i).get(2)*scale.z;
			pointCloud.add(point);
			total_i++;
		}
		
		return new ConvexHullShape(pointCloud);
	}
}
