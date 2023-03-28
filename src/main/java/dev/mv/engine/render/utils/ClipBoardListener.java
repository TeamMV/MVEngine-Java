package dev.mv.engine.render.utils;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.utils.Utils;

import java.awt.*;
import java.awt.datatransfer.*;

public class ClipBoardListener extends Thread implements ClipboardOwner {
    private Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    private String data;

    private static ClipBoardListener instance;

    public static void init() {
        instance = new ClipBoardListener();
        Utils.async(() -> instance.run());
    }

    @Override
    public void run() {
        Transferable trans = sysClip.getContents(this);
        process_clipboard(trans, sysClip);
        TakeOwnership(trans);
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        Transferable contents = sysClip.getContents(this);
        try {
            process_clipboard(contents, c);
        } catch (Exception ex) {
            Exceptions.send(ex);
        }
        TakeOwnership(contents);
    }

    void TakeOwnership(Transferable t) {
        sysClip.setContents(t, this);
    }

    public void process_clipboard(Transferable t, Clipboard c) {
        String tempText;

        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                tempText = (String) t.getTransferData(DataFlavor.stringFlavor);
                data = tempText;
                System.out.println(data);
            }

        } catch (Exception e) {
            Exceptions.send(e);
        }
    }

    public static String getClipboardData() {
        return instance.data;
    }

}