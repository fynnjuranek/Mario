package editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.MouseListener;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

public class GameViewWindow {

    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;
    public void imGui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse |
                ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
        ImGui.endMenuBar();


        // TODO: Mouse is still a little bit on y-axis off
        //ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY());
        ImVec2 windowSize = getLargestSizeForViewport();
        //ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        //ImGui.setCursorScreenPos(windowPos.x, windowPos.y);
        //ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
//        topLeft.x -= ImGui.getScrollX();
//        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

//        leftX = windowPos.x + 10;
//        rightX = windowPos.x + windowSize.x + 10;
//        bottomY = windowPos.y;
//        topY = windowPos.y + windowSize.y;

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        // TODO: Remove -20 and fix the problem
        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y - 20));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));
        ImGui.end();
    }

    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }
    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float viewPortX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewPortY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewPortX + ImGui.getCursorPosX(), viewPortY + ImGui.getCursorPosY());
    }


}
