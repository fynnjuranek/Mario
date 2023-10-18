package components;

import jade.Camera;
import jade.KeyListener;
import jade.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private float dragDebounce = 0.032f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;

    private float lerpTime = 0;
    private boolean reset = false;
    private Camera levelEditorCamera;
    private Vector2f clickOrigin;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0 ) {
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
            return;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt); // to smooth the camera movement / zoom
        }

        // Move camera with middle mouse button
        if (dragDebounce <= 0 && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }

        // To reset the camera to middle point
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            reset = true;
        }

        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime); // lerp to get smooth movement!
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= 5.0f &&
                 Math.abs(levelEditorCamera.position.y) <= 5.0f) {
                this.lerpTime = 0;
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
