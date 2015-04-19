package coffeeblocks.opengl.components;

import java.util.List;

import coffeeblocks.metaobjects.GameObject;
import coffeeblocks.metaobjects.InstantiableObject;

public class CoffeeText{
	public CoffeeText(InstantiableObject letter) {
		letterSource = letter;
		unitSizeX = 1/(float)gridSizeX;
		unitSizeY = 1/(float)gridSizeY;
	}
	//Vi gjør en implementasjon på høyt nivå fordi det er enklere i frohold 
	private InstantiableObject letterSource = null;
	private int gridSizeX = 16;
	private int gridSizeY = 20;
	private float unitSizeX = 0f;
	private float unitSizeY = 0f;
	private String fontSource = null;
	
	public String getFontSource() {
		return fontSource;
	}

	public void setFontSource(String fontSource) {
		this.fontSource = fontSource;
	}

	public GameObject createLetter(char letter){
		int gridX = letter%gridSizeX;
		int gridY = letter/gridSizeX;
		GameObject out = letterSource.createInstance("."+letter, true);
		if(fontSource!=null)
			out.getGameModel().getMaterial().setDiffuseTexture(fontSource);
		float crd_min_x = (float)gridX*unitSizeX;
		float crd_min_y = ((float)gridY)*(unitSizeY+1f/70f);
		float crd_max_x = crd_min_x+unitSizeX;
		float crd_max_y = crd_min_y+unitSizeY;
		List<CoffeeVertex> verts = out.getGameModel().getVertices();
		{
			//Face 1
			verts.get(0).texCoord.x = crd_min_x;
			verts.get(0).texCoord.y = crd_max_y;
			
			verts.get(1).texCoord.x = crd_max_x;
			verts.get(1).texCoord.y = crd_max_y;
			
			verts.get(2).texCoord.x = crd_min_x;
			verts.get(2).texCoord.y = crd_min_y;
			
			//Face 2
			verts.get(3).texCoord.x = crd_max_x;
			verts.get(3).texCoord.y = crd_max_y;
			
			verts.get(4).texCoord.x = crd_max_x;
			verts.get(4).texCoord.y = crd_min_y;
			
			verts.get(5).texCoord.x = crd_min_x;
			verts.get(5).texCoord.y = crd_min_y;
		}
		return out;
	}
}
