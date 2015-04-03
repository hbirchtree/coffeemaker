package coffeeblocks.opengl.components;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class CoffeeCamera {
	
	private Vector3f cameraPos = new Vector3f(3,10,5);
	public Vector3f getCameraPos() {
		return cameraPos;
	}
	public void setCameraPos(Vector3f cameraPos) {
		this.cameraPos = cameraPos;
	}
	public float getFieldOfView() {
		return fieldOfView;
	}
	public void setFieldOfView(float fieldOfView) {
		this.fieldOfView = fieldOfView;
	}
	
	private float aspect = 16/9f;
	private float fieldOfView = 90f;
	private float zNear = 0.1f;
	private float zFar = 200f;
	
	private float horizAngle = 0f;
	private float vertiAngle = 0f;
	private float vertiAngleMax = 85f;
	
	public float getHorizAngle() {
		return horizAngle;
	}
	public float getVertiAngle() {
		return vertiAngle;
	}
	
	public void lookAt(Vector3f targetPos){
		Vector3f direction = Vector3f.sub(targetPos, cameraPos, null);
		direction.normalise();
		vertiAngle = -(float)Math.toRadians(Math.asin(-direction.y));
		horizAngle = -(float)Math.toRadians(Math.atan2(-direction.x,-direction.z));
		normalizeAngles();
	}
	
	public void offsetOrientation(float rightAngle,float upAngle){
		horizAngle += rightAngle;
		vertiAngle += upAngle;
		normalizeAngles();
	}
	
	public void moveCameraForward(float scalar){
		Vector3f direction = getForward();
		Vector3f displace = new Vector3f();
		displace.x = direction.x*scalar;
		displace.y = direction.y*scalar;
		displace.z = direction.z*scalar;
		Vector3f.add(getCameraPos(), displace, displace);
		setCameraPos(displace);
	}
	public Vector3f getCameraForwardVec(float scalar){
		Vector3f direction = getForward();
		Vector3f displace = new Vector3f();
		displace.x = direction.x*scalar;
		displace.y = direction.y*scalar;
		displace.z = direction.z*scalar;
		return displace;
	}
	public void moveCameraRight(float scalar){
		Vector3f direction = getRight();
		Vector3f displace = new Vector3f();
		displace.x = direction.x*scalar;
		displace.y = direction.y*scalar;
		displace.z = direction.z*scalar;
		Vector3f.add(getCameraPos(), displace, displace);
		setCameraPos(displace);
	}
	public Vector3f getCameraRightVec(float scalar){
		Vector3f direction = getRight();
		Vector3f displace = new Vector3f();
		displace.x = direction.x*scalar;
		displace.y = direction.y*scalar;
		displace.z = direction.z*scalar;
		return displace;
	}
	
	public void offsetPosition(Vector3f offset){
		setCameraPos(Vector3f.add(getCameraPos(), offset, null));
	}
	
	public Matrix4f getOrientation(){
		Matrix4f result = new Matrix4f();
		Matrix4f.rotate((float)Math.toRadians(vertiAngle), new Vector3f(1,0,0), result, result);
		Matrix4f.rotate((float)Math.toRadians(horizAngle), new Vector3f(0,1,0), result, result);
		return result;
	}
	
	public Vector3f getUp(){
		Matrix4f forward = new Matrix4f();
		Matrix4f.invert(getOrientation(), forward);
		Vector4f forwardVec = new Vector4f();
		Matrix4f.transform(forward,new Vector4f(0,1,0,1),forwardVec);
		return new Vector3f(forwardVec);
	}
	public Vector3f getRight(){
		Matrix4f forward = new Matrix4f();
		Matrix4f.invert(getOrientation(), forward);
		Vector4f forwardVec = new Vector4f();
		Matrix4f.transform(forward,new Vector4f(1,0,0,1),forwardVec);
		return new Vector3f(forwardVec);
	}
	public Vector3f getForward(){
		Matrix4f forward = new Matrix4f();
		Matrix4f.invert(getOrientation(), forward);
		Vector4f forwardVec = new Vector4f();
		Matrix4f.transform(forward,new Vector4f(0,0,-1,1),forwardVec);
		return new Vector3f(forwardVec);
	}
	
	public FloatBuffer matrix(){
		Matrix4f matrix = new Matrix4f();
		Matrix4f.mul(getProjection(), getView(), matrix);
		FloatBuffer matBuf = BufferUtils.createFloatBuffer(16);
		matrix.store(matBuf);
		matBuf.flip();
		return matBuf;
	}
	public FloatBuffer matrixOrtho(){
		Matrix4f matrix = new Matrix4f();
		Matrix4f.mul(getProjection(), new Matrix4f(), matrix);
		FloatBuffer matBuf = BufferUtils.createFloatBuffer(16);
		matrix.store(matBuf);
		matBuf.flip();
		return matBuf;
	}
	
	public Matrix4f getView(){
		Matrix4f view = new Matrix4f();
		Matrix4f pos = new Matrix4f();
		Vector3f cameraPosNeg = new Vector3f(cameraPos);
		cameraPosNeg.negate();
		Matrix4f.translate(cameraPosNeg, pos, pos);
		Matrix4f.mul(getOrientation(), pos, view);
		return view;
	}
	
	public Matrix4f getProjection(){
		return CoffeeCamera.gluPerspective(aspect,fieldOfView,zNear,zFar);
	}
	
	public void normalizeAngles(){
		horizAngle = horizAngle%360f;
		if(horizAngle<0f)
			horizAngle += 360f;
		
		if(vertiAngle>vertiAngleMax)
			vertiAngle = vertiAngleMax;
		else if(vertiAngle<-vertiAngleMax)
			vertiAngle = -vertiAngleMax;
	}
	
	public static FloatBuffer genProjection(float aspect,float fov,float znear,float zfar){
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		CoffeeCamera.gluPerspective(aspect,fov,znear,zfar).store(projection);
		projection.flip();
		return projection;
	}
	
	public static FloatBuffer genCamera(Vector3f eyePos, Vector3f centerPos, Vector3f upVector){
		FloatBuffer camera = BufferUtils.createFloatBuffer(16);
		CoffeeCamera.gluLookAt(eyePos,centerPos,upVector).store(camera);
		camera.flip();
		return camera;
	}
	
	public static Matrix4f gluPerspective(float aspect, float fov, float znear, float zfar){
		Matrix4f m = new Matrix4f();
		
		float y_scale = (float) (1f/Math.tan(Math.toRadians(fov / 2f)));
		float x_scale = y_scale / aspect;
		float frustum_length = zfar - znear;

		m.m00 = x_scale;
		m.m11 = y_scale;
		m.m22 = -((zfar + znear) / frustum_length);
		m.m23 = -1;
		m.m32 = -((2 * znear * zfar) / frustum_length);
		m.m33 = 0;
		
		return m;
	}
	
	public static Matrix4f gluLookAt(Vector3f eye, Vector3f center, Vector3f up){
		Matrix4f view = new Matrix4f();
		Vector3f x = new Vector3f();
		Vector3f y = new Vector3f();
		Vector3f z = new Vector3f();
		
		Vector3f.sub(eye, center, z);
		z.normalise();
		y = up;
		Vector3f.cross(y, z, x);
		Vector3f.cross(z,x,y);
		
		x.normalise();
		y.normalise();
		
		view.m00 = x.x;
		view.m01 = x.y;
		view.m02 = x.z;
		view.m03 = -Vector3f.dot(x, eye);
		
		view.m10 = y.x;
		view.m11 = y.y;
		view.m12 = y.z;
		view.m13 = -Vector3f.dot(y, eye);
		
		view.m20 = z.x;
		view.m21 = z.y;
		view.m22 = z.z;
		view.m23 = -Vector3f.dot(z, eye);
		
		view.m30 = 0;
		view.m31 = 0;
		view.m32 = 0;
		view.m33 = 1.0f;
		
		return view;
	}

}
