package coffeeblocks.foundation.logic;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import coffeeblocks.foundation.CoffeeSceneManager;
import coffeeblocks.foundation.logic.CoffeeShop.SceneApplier;
import coffeeblocks.metaobjects.Vector3Container;
import coffeeblocks.opengl.CoffeeAnimator;

public class CoffeeMenuScene extends CoffeeSceneTemplate{
	protected static final String SCENE_ID_SECOND = "second";
	public CoffeeMenuScene(CoffeeSceneManager manager, CoffeeAnimator animator, SceneApplier sceneApplier){super(manager, animator, sceneApplier);}
	@Override
	public String getSceneId() {return SCENE_ID_SECOND;}
	@Override
	public void setupSpecifics() {
		{
			CoffeeTextStruct titleText = new CoffeeTextStruct(new Vector3Container.VectorOffsetCallback(){
				@Override
				public Vector3f getOffset() {
					return getScene().getCamera().getCameraRightVec(2f);
				}
			});
			titleText.getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
			titleText.getPosition().setValueOffset(new Vector3f(0,2f,0));
			billboardContainer(titleText.getRotation(),true);
			float _scale = 0.4f;
			titleText.getScale().setValue(new Vector3f(_scale,_scale,_scale));
			writeSentence(titleText,"CoffeeMaker");
			sentences.put(OBJECT_ID_OVERLAY, titleText);
		}
		{
			CoffeeTextStruct startText = new CoffeeTextStruct(new Vector3Container.VectorOffsetCallback(){
				@Override
				public Vector3f getOffset() {
					return getScene().getCamera().getCameraRightVec(0.7f);
				}
			});
			startText.getPosition().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
			startText.getPosition().setValueOffset(new Vector3f(0,-1f,0));
			billboardContainer(startText.getRotation(),true);
			float _scale = 0.15f;
			startText.getScale().setValue(new Vector3f(_scale,_scale,_scale));
			writeSentence(startText,"Press 'ENTER' to start");
			sentences.put(OBJECT_ID_OVERLAY, startText);
		}
		getScene().getCamera().getCameraPos().bindValue(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition());
		getScene().getCamera().getCameraPos().setOffsetCallback(new Vector3Container.VectorOffsetCallback() {
			@Override
			public Vector3f getOffset() {
				return getScene().getCamera().getCameraForwardVec(-5f);
			}
		});
		rotationTimer = System.currentTimeMillis()+1000;
//		System.out.println(getScene().getCamera().getCameraRotation().getValue().toString());
	}

	private long rotationTimer = 0l;
//	private float ori = 0f;
	private float ori_r = 0.09f;
	private float ori_u = 0.05f;
	@Override
	public void tickSpecifics() {
		super.tickSpecifics();
		if(System.currentTimeMillis()>=rotationTimer){
			getScene().getCamera().offsetOrientation(ori_r,ori_u);
			if(getScene().getCamera().getCameraRotation().getValue().x>60f||getScene().getCamera().getCameraRotation().getValue().x<10.01f)
				ori_u = -ori_u;
			rotationTimer = System.currentTimeMillis()+10;
		}
	}

	@Override public void handleKeyPress(int key){
		switch(key){
		case GLFW.GLFW_KEY_ENTER:{
			applyScene("main");
			return;
		}
		case GLFW.GLFW_KEY_J:{
			getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().setValue(new Vector3f());
			animator.addTransition(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition(), new Vector3f(0,15,0), CoffeeAnimator.TransitionType.ValueLinear, 1000);
			return;
		}
		case GLFW.GLFW_KEY_K:{
			getObject(OBJECT_ID_PLAYER).getGameModel().getPosition().setValue(new Vector3f());
			animator.addTransition(getObject(OBJECT_ID_PLAYER).getGameModel().getPosition(), new Vector3f(0,-15,0), CoffeeAnimator.TransitionType.ValueLinear, 1000);
			return;
		}
		}
	}
	
	@Override public void setupCamera(){}
	@Override public void tickCamera(){}
	@Override public void handleMouseMove(double x,double y){/*Vi overstyrer denne for å slå av den vanlige funksjonen i CoffeeSceneTemplate*/}
	@Override public void handleKeyRelease(int key) {}
	@Override public void handleMousePress(int key) {}
	@Override public void handleMouseRelease(int key) {}
	@Override public void handleCollisions(String body1, String body2) {}
	@Override public void onGlfwFrameTick(double currentTime) {}
}
