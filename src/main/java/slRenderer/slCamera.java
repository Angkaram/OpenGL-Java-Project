package slRenderer;


import org.joml.Matrix4f;
import org.joml.Vector3f;

import static csc133.spot.*;

// interface is not required but once you have it you must implement it
interface CameraInterface {
    public void setOrthoProjection();
    public void relativeMoveCamera(float deltaX, float deltaY);
    public Vector3f getCurLookFrom();
    public void setCurLookFrom(Vector3f new_look_from);
    public Vector3f getCurLookAt();
    public Matrix4f getViewMatrix();
    public Matrix4f getProjectionMatrix();
}

public class slCamera implements CameraInterface {
    private final Matrix4f projectionMatrix, viewMatrix;
    public Vector3f defaultLookFrom = new Vector3f(0.0f, 0.0f, 0.0f);
    public Vector3f defaultLookAt = new Vector3f(0.0f, 0.0f, -1.0f);
    public Vector3f defaultUpVector = new Vector3f(0.0f, 1.0f, 0.0f);

    private Vector3f curLookFrom = new Vector3f(defaultLookFrom);
    private final Vector3f curLookAt   = new Vector3f(defaultLookAt);
    private final Vector3f curUpVector = new Vector3f(defaultUpVector);

    public slCamera(Vector3f camera_position) {
        this.defaultLookFrom = camera_position;
        this.curLookFrom = camera_position;
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.identity();
        this.viewMatrix = new Matrix4f();
        this.viewMatrix.identity();
        setOrthoProjection();
    }

    @Override
    public void relativeMoveCamera(float deltaX, float deltaY) {
        this.curLookFrom.x -= deltaX;
        this.curLookFrom.y -= deltaY;
    }

    @Override
    public Vector3f getCurLookFrom() {

        return this.curLookFrom;
    }

    @Override
    public void setCurLookFrom(Vector3f new_look_from) {

        this.curLookFrom.set(new_look_from);
    }

    @Override
    public Matrix4f getViewMatrix() {
        curLookFrom.set(defaultLookFrom);
        curLookAt.set(defaultLookAt);
        this.viewMatrix.identity();
        this.viewMatrix.lookAt(curLookFrom, curLookAt.add(defaultLookFrom), curUpVector);

        return this.viewMatrix;
    }

    @Override
    public Matrix4f getProjectionMatrix() {

        return this.projectionMatrix;
    }
    @Override
    public void setOrthoProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(FRUSTUM_LEFT, FRUSTUM_RIGHT,
                FRUSTUM_BOTTOM, FRUSTUM_TOP, Z_NEAR, Z_FAR);
    }

    @Override
    public Vector3f getCurLookAt() {
        return this.curLookAt;
    }

    // fall back to the default values
    public void restoreCamera() {
        defaultLookFrom.x = 0.0f;
        defaultLookFrom.y = 0.0f;
        setOrthoProjection();
    }
}
