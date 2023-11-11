import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import java.awt.event.InputEvent;

import java.awt.*;

public class OpenCV {
    private VideoCapture camera;
    private CascadeClassifier faceDetector;
    private org.opencv.core.Point lastFaceCenter = null;
    private Robot robot;
    private double nodThreshold = 100.0; // 触发阈值 Trigger threshold

    public OpenCV() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        camera = new VideoCapture(0);
        faceDetector = new CascadeClassifier("haarcascade_frontalface_default.xml");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        Mat frame = new Mat();
        camera.read(frame);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(frame, faceDetections);

        for (Rect rect : faceDetections.toArray()) {
            org.opencv.core.Point currentFaceCenter = new org.opencv.core.Point(rect.x + rect.width / 2, rect.y + rect.height / 2);

            if (lastFaceCenter != null) {
                double rawDeltaY = lastFaceCenter.y - currentFaceCenter.y; // 获取脸部的上下移动值 Get the up and down movement value of the face

                // 当脸部向下移动超过设置的阈值时，模拟鼠标点击 When the face moves down more than the set threshold, simulate mouse click
                if (rawDeltaY > nodThreshold) {
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
            }

            lastFaceCenter = currentFaceCenter;
        }
    }

    public static void main(String[] args) {
        OpenCV app = new OpenCV();
        while(true) {
            app.update();
        }
    }
}
