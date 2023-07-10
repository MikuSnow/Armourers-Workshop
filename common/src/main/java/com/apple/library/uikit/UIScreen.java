package com.apple.library.uikit;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.ScreenImpl;

public class UIScreen extends ScreenImpl {

    public static CGRect convertRectFromView(CGRect rect, UIView view) {
        // the view must display, if not ignore it.
        UIWindow window = view.window();
        if (window == null) {
            return rect;
        }
        // we can only convert to the window coordinate,
        // so add window offset convert to screen coordinate.
        rect = view.presentation().convertRectToView(rect, window);
        CGRect frame = window.frame();
        if (frame.x == 0 && frame.y == 0) {
            return rect;
        }
        return rect.offset(frame.x, frame.y);
    }
}
