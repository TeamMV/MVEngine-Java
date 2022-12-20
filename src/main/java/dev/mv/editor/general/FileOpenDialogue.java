package dev.mv.editor.general;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.async.Promise;
import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;

import java.io.File;

public class FileOpenDialogue {
    private static File output = null;
    private static boolean isOpen = false;

    public static File open(String rootDirectory, Target target) {
        isOpen = true;

        ImGui.begin(target == Target.FILE ? "choose your file" : "choose your directories", ImGuiWindowFlags.NoCollapse);
        ImGui.beginChild("dirs");
        ImGui.textColored(0, 179, 255, 255, "Directories");
        ImGui.setNextItemWidth(100);
        ImGui.selectable("home");
        ImGui.endChild();

        if(ImGui.button("cancel")) {
            isOpen = false;
        }
        ImGui.end();

        return output;
    }

    public static boolean isOpen() {
        return isOpen;
    }

    public enum Target{
        FILE,
        DIRECTORY
    }
}
