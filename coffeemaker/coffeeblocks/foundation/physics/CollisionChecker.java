package coffeeblocks.foundation.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import coffeeblocks.foundation.CoffeeGameObjectManager;
import coffeeblocks.foundation.CoffeeGameObjectManagerListener;
import coffeeblocks.foundation.CoffeeRendererListener;
import coffeeblocks.general.VectorTools;
import coffeeblocks.metaobjects.GameObject;

public class CollisionChecker implements CoffeeGameObjectManagerListener,CoffeeRendererListener{
	
	private Map<String,RigidBody> objects = new HashMap<>();
	
	private Vector3f gravity = new Vector3f(0f,-9.81f,0f);
	public Vector3f getGravity() {
		return gravity;
	}
	public void setGravity(Vector3f gravity) {
		this.gravity = gravity;
		dynamicsWorld.setGravity(gravity);
	}
	
	private DynamicsWorld dynamicsWorld;
	
	private CoffeeGameObjectManager manager = null;
	
	public CollisionChecker(CoffeeGameObjectManager manager){
		if(manager==null)
			throw new RuntimeException("Failed to initialize physics: No object manager");
		this.manager = manager;
		this.manager.addListener(this);
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collideConfig = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatch = new CollisionDispatcher(collideConfig);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatch,broadphase,solver,collideConfig);
		dynamicsWorld.setGravity(gravity);
		dynamicsWorld.setInternalTickCallback(new InternalTickCallback(){
			@Override
			public void internalTick(DynamicsWorld world, float timeStep) {
				// TODO Auto-generated method stub
				Dispatcher dispatch = world.getDispatcher();
				for(int i=0;i<dispatch.getNumManifolds();i++){
					PersistentManifold man = dispatch.getManifoldByIndexInternal(i);
					RigidBody b1 = (RigidBody)man.getBody0();
					RigidBody b2 = (RigidBody)man.getBody1();
					String g1 = ((GameObject)b1.getUserPointer()).getObjectId();
					String g2 = ((GameObject)b2.getUserPointer()).getObjectId();
					for(int a=0;a<man.getNumContacts();a++){
						ManifoldPoint cPoint = man.getContactPoint(a);
						if(cPoint.getDistance()<0f)
							for(CollisionListener listener : listeners)
								listener.getCollisionNotification(g1, g2);
					}
				}
			}
		}, null);
	}
	public void createCollisionObject(GameObject object){
		if(objects.containsKey(object.getObjectId()))
			throw new IllegalArgumentException("Object "+object.getObjectId()+" has already been added to the physics world!");
		CollisionShape shape = null;
		switch(object.getGameModel().getPhysicsType()){
		case Complex:
			shape = TriangleMeshHelper.createTriangleMesh(object.getGameModel().getCollisionMeshFile(),VectorTools.lwjglToVMVec3f(object.getGameModel().getPhysicalScale()));
			break;
		case Box:
			shape = new BoxShape(VectorTools.lwjglToVMVec3f(object.getGameModel().getPhysicalScale()));
			break;
		case Sphere:
			shape = new SphereShape(object.getGameModel().getPhysicalScale().x);
			break;
		case Capsule:
			shape = new CapsuleShape(object.getGameModel().getPhysicalScale().x,object.getGameModel().getPhysicalScale().y);
			break;
		case StaticPlane:
			shape = new StaticPlaneShape(VectorTools.lwjglToVMVec3f(object.getGameModel().getPhysicalRotation()),object.getGameModel().getPhysicalScale().y);
			break;
		default:
			break;
		}
		if(shape==null)
			throw new IllegalArgumentException("Object "+object.getObjectId()+" could not be added to the physics world!");
		
//		Vector3f protation = VectorTools.lwjglToVMVec3f(object.getGameModel().getPhysicalRotation());
		MotionState motionState = new DefaultMotionState(createTransform(VectorTools.lwjglToVMVec3f(object.getGameModel().getPosition())));
		Vector3f inertia = VectorTools.lwjglToVMVec3f(object.getGameModel().getPhysicalInertia());
		if(object.getGameModel().getPhysicalMass()!=0f)
			shape.calculateLocalInertia(object.getGameModel().getPhysicalMass(), inertia);
		RigidBodyConstructionInfo constInfo = new RigidBodyConstructionInfo(object.getGameModel().getPhysicalMass(),motionState,shape, inertia);
		constInfo.restitution = object.getGameModel().getRestitution();
		constInfo.friction = object.getGameModel().getFriction();

		RigidBody body = new RigidBody(constInfo);
		if(!(shape instanceof BvhTriangleMeshShape)){
			body.setInvInertiaDiagLocal(VectorTools.lwjglToVMVec3f(object.getGameModel().getPhysicalLinearFactor()));
			body.updateInertiaTensor();
		}
		body.setUserPointer(object);
		objects.put(object.getObjectId(), body);
		dynamicsWorld.addRigidBody(body);
	}
	
	private Transform createTransform(Vector3f position){
		Transform t = new Transform();
		t.setIdentity();
		t.origin.set(position);
		return t;
	}
	
	@Override
	public void onGlfwFrameTick(float tickTime){
		dynamicsWorld.stepSimulation(tickTime*100f);
		for(String id : objects.keySet()){
			RigidBody body = objects.get(id);
			manager.getObject(id).getGameModel().setPosition(VectorTools.vmVec3ftoLwjgl(body.getWorldTransform(new Transform()).origin));
//			Quat4f rotation = new Quat4f();
//			body.getWorldTransform(new Transform()).getRotation(rotation);
//			Vector3f rot = new Vector3f();
//			manager.getObject(id).getGameModel().setRotation(VectorTools.vmVec3ftoLwjgl());
			for(CollisionListener listener : listeners)
				listener.updateObject(id);
		}
	}
	
	private List<CollisionListener> listeners = new ArrayList<>();
	public void addCollisionListener(CollisionListener listener){
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	public boolean performRaytest(org.lwjgl.util.vector.Vector3f start,String targetObject){
		RigidBody target = objects.get(targetObject);
		Vector3f end = target.getWorldTransform(new Transform()).origin;
		CollisionWorld.ClosestRayResultCallback ray = new CollisionWorld.ClosestRayResultCallback(VectorTools.lwjglToVMVec3f(start), end);
		dynamicsWorld.rayTest(VectorTools.lwjglToVMVec3f(start), end, ray);
		if(ray.hasHit()&&target==(RigidBody)ray.collisionObject){ //Vi vil vite at det er det absolutt samme objektet vi spør etter. Strålen kan treffe mye annet.
			return true;
		}
		return false;
	}
	
	public String performRaytestId(org.lwjgl.util.vector.Vector3f start,org.lwjgl.util.vector.Vector3f end){
		CollisionWorld.ClosestRayResultCallback ray = new CollisionWorld.ClosestRayResultCallback(VectorTools.lwjglToVMVec3f(start), VectorTools.lwjglToVMVec3f(end));
		dynamicsWorld.rayTest(VectorTools.lwjglToVMVec3f(start), VectorTools.lwjglToVMVec3f(end), ray);
		if(ray.hasHit()){ //Vi vil vite at det er det absolutt samme objektet vi spør etter. Strålen kan treffe mye annet.
			return ((GameObject)((RigidBody)ray.collisionObject).getUserPointer()).getObjectId();
		}
		return null;
	}
	
	@Override
	public void existingGameObjectChanged(String objectId,GameObject.PropertyEnumeration property){
		if(!objects.containsKey(objectId))
			return;
		RigidBody body = objects.get(objectId);
		switch(property){
		case PHYS_POS:
			body.activate(true);
			body.setWorldTransform(createTransform(VectorTools.lwjglToVMVec3f(manager.getObject(objectId).getGameModel().getPosition())));
			break;
		case PHYS_CLEARFORCE:
			body.setLinearVelocity(new Vector3f());
			body.setAngularVelocity(new Vector3f());
			body.clearForces();
			break;
		case PHYS_ACCEL:
			body.applyCentralForce(VectorTools.lwjglToVMVec3f(manager.getObject(objectId).getGameModel().getPositionalAcceleration()));			
			break;
		case PHYS_IMPULSE:
			body.applyCentralImpulse(VectorTools.lwjglToVMVec3f(manager.getObject(objectId).getGameModel().getImpulse()));
			manager.getObject(objectId).getGameModel().setImpulse(new org.lwjgl.util.vector.Vector3f());
			break;
		case PHYS_ACTIVATION:
			if(!manager.getObject(objectId).getGameModel().getObjectDeactivation())
				body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
			break;
		default:
			break;
		}
	}

	@Override
	public void newGameObjectAdded(GameObject object){
		try{
			createCollisionObject(object);
		}catch(IllegalArgumentException e){
			System.err.println(e.getMessage());
		}
	}
}
